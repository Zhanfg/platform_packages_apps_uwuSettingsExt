"""Regression check for the gesture navigation mode used to enable the hide-pill switch."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[2]
SRC=ROOT/"src/org/uwuaosp/settingsext/interfaceui/InterfaceSettingsActivity.kt"
class HideGestureEntryTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.src=SRC.read_text(encoding="utf-8")
    def test_framework_overlay_is_authority(self):
        src=self.src.split("fun isGestureNavigation()",1)[1].split("var gestureNavigation",1)[0]
        self.assertIn("context.resources.getInteger(",src)
        self.assertIn("config_navBarInteractionMode",src)
        self.assertNotIn("Settings.Secure.getInt(",src)
    def test_secure_switch_value_and_notifications_preserved(self):
        self.assertIn("Settings.Secure.UWU_HIDE_GESTURE_HANDLE",self.src)
        self.assertIn("Settings.Secure.putInt(",self.src)
        self.assertIn("hideGestureHandle = hidden",self.src)
        self.assertIn("enabled = gestureNavigation",self.src)
    def test_switch_updates_on_navigation_mode_change(self):
        self.assertIn("Settings.Secure.getUriFor(Settings.Secure.NAVIGATION_MODE)",self.src)
        self.assertIn("gestureNavigation = isGestureNavigation()",self.src)
if __name__=="__main__":unittest.main()
