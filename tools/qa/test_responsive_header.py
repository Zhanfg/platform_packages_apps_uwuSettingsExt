"""Guard homepage illustration size at compact logical widths.

Static checks only; Android Compose compilation and visual test are still required.
"""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "src/org/uwuaosp/settingsext/SettingsExtActivity.kt"


class SettingsExtResponsiveHeaderTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.text = SOURCE.read_text(encoding="utf-8")

    def test_dpi_independent_width_breakpoints(self):
        self.assertIn("LocalConfiguration.current.screenWidthDp", self.text)
        self.assertIn("screenWidthDp < 360 -> 136.dp", self.text)
        self.assertIn("screenWidthDp < 400 -> 156.dp", self.text)
        self.assertIn("else -> 180.dp", self.text)

    def test_header_consumes_adaptive_height(self):
        self.assertIn("height = illustrationHeight,", self.text)
        self.assertNotIn("height = 180.dp,", self.text)

    def test_compose_settings_theme_kept(self):
        self.assertIn("SettingsExtTheme {", self.text)
        self.assertIn("SettingsScaffold(", self.text)


if __name__ == "__main__":
    unittest.main()
