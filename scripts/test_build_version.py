import unittest
from build_version import next_version, version_fields


class BuildVersionTest(unittest.TestCase):
    def test_small_change_and_android_code(self):
        state = next_version({"year": 2026, "major": 0, "patch": 1}, 2026)
        self.assertEqual(("2026.0.2", 20260002), version_fields(state))

    def test_99_and_explicit_major(self):
        self.assertEqual({"year": 2026, "major": 3, "patch": 99},
                         next_version({"year": 2026, "major": 3, "patch": 98}, 2026))
        self.assertEqual({"year": 2026, "major": 4, "patch": 0},
                         next_version({"year": 2026, "major": 3, "patch": 99}, 2026))
        self.assertEqual({"year": 2026, "major": 4, "patch": 0},
                         next_version({"year": 2026, "major": 3, "patch": 12}, 2026, True))

    def test_new_year_reset_and_major(self):
        old = {"year": 2026, "major": 3, "patch": 45}
        self.assertEqual({"year": 2027, "major": 0, "patch": 1}, next_version(old, 2027))
        self.assertEqual({"year": 2027, "major": 1, "patch": 0}, next_version(old, 2027, True))
        self.assertGreater(version_fields(next_version(old, 2027))[1], version_fields(old)[1])

    def test_invalid_or_non_monotonic_inputs_fail_closed(self):
        for state, year in [({"year": 2027, "major": 0, "patch": 1}, 2026),
                            ({"year": 2026, "major": 99, "patch": 99}, 2026),
                            ({"year": 2026, "major": 0, "patch": True}, 2026),
                            ({"year": 2026, "major": -1, "patch": 1}, 2026),
                            ({"year": 2026, "patch": 1}, 2026)]:
            with self.assertRaises(ValueError):
                next_version(state, year)


if __name__ == "__main__":
    unittest.main()
