"""Reserve one monotonically increasing Android version for a hosted build.

The dedicated branch contains only the counter. A GitHub contents update uses the
previous blob SHA as an optimistic lock; a conflicting update fails closed.
"""
import base64
import datetime
import json
import os
import urllib.error
import urllib.parse
import urllib.request
from zoneinfo import ZoneInfo

BRANCH = "build-version-state"
PATH = ".build-version.json"


def next_version(state, year, major_release=False):
    if set(state) != {"year", "major", "patch"}:
        raise ValueError("Invalid version state fields")
    if any(type(state[k]) is not int for k in state):
        raise ValueError("Version state must contain integers")
    old_year, major, patch = state["year"], state["major"], state["patch"]
    if not (2000 <= old_year <= 2099 and 0 <= major <= 99 and 0 <= patch <= 99):
        raise ValueError("Version state out of range")
    if not 2000 <= year <= 2099 or year < old_year:
        raise ValueError("Invalid year or clock moved backwards")
    if year > old_year:
        major, patch = (1, 0) if major_release else (0, 1)
    elif major_release or patch == 99:
        major, patch = major + 1, 0
    else:
        patch += 1
    if major > 99:
        raise ValueError("Major limit reached; do not reuse an Android versionCode")
    return {"year": year, "major": major, "patch": patch}


def effective_major_request(requested, run_id, previously_marked):
    """An all-jobs retry reserves a new patch, not a second major for one event."""
    if not isinstance(run_id, str) or not run_id.isdigit():
        raise ValueError("Invalid run ID")
    if not isinstance(previously_marked, list) or not all(isinstance(x, str) and x.isdigit() for x in previously_marked):
        raise ValueError("Invalid major event history")
    return bool(requested and run_id not in previously_marked)


def version_fields(state):
    year, major, patch = state["year"], state["major"], state["patch"]
    code = year * 10000 + major * 100 + patch
    if code > 2_100_000_000:
        raise ValueError("Android versionCode limit exceeded")
    return f"{year}.{major}.{patch}", code


def request(url, token, method="GET", data=None):
    headers = {"Authorization": f"Bearer {token}", "Accept": "application/vnd.github+json",
               "User-Agent": "Hidayah-build-version", "X-GitHub-Api-Version": "2022-11-28"}
    if data is not None:
        headers["Content-Type"] = "application/json"
    req = urllib.request.Request(url, headers=headers, method=method,
                                 data=None if data is None else json.dumps(data).encode())
    with urllib.request.urlopen(req, timeout=30) as response:
        return json.load(response)


def main():
    token = os.environ["GITHUB_TOKEN"]
    repo = os.environ["GITHUB_REPOSITORY"]
    url = f"{os.environ['GITHUB_API_URL']}/repos/{repo}/contents/{PATH}"
    current = request(url + "?ref=" + urllib.parse.quote(BRANCH), token)
    ledger = json.loads(base64.b64decode(current["content"]).decode("utf-8"))
    major_runs = ledger.pop("major_runs", [])
    run_id = os.environ["GITHUB_RUN_ID"]
    today = datetime.datetime.now(ZoneInfo("Asia/Vientiane")).year
    major = effective_major_request(os.environ.get("MAJOR_RELEASE", "false").lower() == "true", run_id, major_runs)
    updated = next_version(ledger, today, major)
    name, code = version_fields(updated)
    if major:
        major_runs.append(run_id)
    updated["major_runs"] = major_runs
    payload = {
        "message": f"Reserve version {name} for build {os.environ['GITHUB_RUN_ID']} attempt {os.environ['GITHUB_RUN_ATTEMPT']}",
        "content": base64.b64encode((json.dumps(updated, separators=(",", ":")) + "\n").encode()).decode(),
        "sha": current["sha"], "branch": BRANCH,
    }
    request(url, token, "PUT", payload)
    with open(os.environ["GITHUB_OUTPUT"], "a", encoding="utf-8") as output:
        output.write(f"name={name}\ncode={code}\nattempt={os.environ['GITHUB_RUN_ATTEMPT']}\n")
    print(f"Reserved {name} (versionCode {code}). Failed builds can leave gaps.")


if __name__ == "__main__":
    main()
