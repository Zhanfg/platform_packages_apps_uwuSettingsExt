/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uwuaosp.settingsext.util

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import org.uwuaosp.settingsext.R
import com.google.android.material.R as MaterialR
import org.uwuaosp.compose.settingslib.SettingsHomepageIcon as ComposeSettingsHomepageIcon

@Composable
internal fun SettingsHomepageIcon(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
) {
    val palette = homepageIconPalette(iconRes)
    val darkTheme = isSystemInDarkTheme()

    ComposeSettingsHomepageIcon(
        iconRes = iconRes,
        modifier = modifier,
        backgroundColor = colorResource(if (darkTheme) palette.darkBackground else palette.background),
        foregroundColor = colorResource(palette.foreground),
    )
}

// Use the same fixed reference colors as Settings' homepage icons.
private enum class HomepageIconPalette(
    @ColorRes val foreground: Int,
    @ColorRes val background: Int,
    @ColorRes val darkBackground: Int,
) {
    Orange(
        MaterialR.color.m3_ref_palette_orange30,
        MaterialR.color.m3_ref_palette_orange90,
        MaterialR.color.m3_ref_palette_orange80,
    ),
    Green(
        MaterialR.color.m3_ref_palette_green30,
        MaterialR.color.m3_ref_palette_green90,
        MaterialR.color.m3_ref_palette_green80,
    ),
    Cyan(
        MaterialR.color.m3_ref_palette_cyan30,
        MaterialR.color.m3_ref_palette_cyan90,
        MaterialR.color.m3_ref_palette_cyan80,
    ),
    Pink(
        MaterialR.color.m3_ref_palette_pink30,
        MaterialR.color.m3_ref_palette_pink90,
        MaterialR.color.m3_ref_palette_pink80,
    ),
    Purple(
        MaterialR.color.m3_ref_palette_purple30,
        MaterialR.color.m3_ref_palette_purple90,
        MaterialR.color.m3_ref_palette_purple80,
    ),
    BlueVariant(
        MaterialR.color.m3_ref_palette_blue_variant30,
        MaterialR.color.m3_ref_palette_blue_variant90,
        MaterialR.color.m3_ref_palette_blue_variant80,
    ),
    Blue(
        MaterialR.color.m3_ref_palette_blue30,
        MaterialR.color.m3_ref_palette_blue90,
        MaterialR.color.m3_ref_palette_blue80,
    ),
    Yellow(
        MaterialR.color.m3_ref_palette_yellow30,
        MaterialR.color.m3_ref_palette_yellow90,
        MaterialR.color.m3_ref_palette_yellow80,
    ),
}

private fun homepageIconPalette(@DrawableRes iconRes: Int): HomepageIconPalette = when (iconRes) {
    R.drawable.ic_interface_settings,
    R.drawable.ic_custom_font,
    R.drawable.ic_system_small_icons -> HomepageIconPalette.Orange

    R.drawable.ic_background_management,
    R.drawable.ic_background_log_description -> HomepageIconPalette.Green

    R.drawable.ic_sensor_policy,
    R.drawable.ic_spoofing,
    R.drawable.ic_clipboard_policy -> HomepageIconPalette.Cyan

    R.drawable.ic_moment,
    R.drawable.ic_moment_arc_edit,
    R.drawable.ic_statusbarlyric -> HomepageIconPalette.Pink

    R.drawable.ic_launcher_settings -> HomepageIconPalette.Purple
    R.drawable.ic_external_desktop -> HomepageIconPalette.BlueVariant
    R.drawable.ic_appjump -> HomepageIconPalette.Blue
    R.drawable.ic_smart_suggestions -> HomepageIconPalette.Yellow
    R.drawable.ic_ai -> HomepageIconPalette.Purple
    R.drawable.ic_phone_camera -> HomepageIconPalette.Blue
    else -> HomepageIconPalette.Purple
}
