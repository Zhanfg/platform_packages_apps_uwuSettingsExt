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

package org.uwuaosp.settingsext.moment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import org.uwuaosp.compose.widget.preference.MainSwitchPreference
import org.uwuaosp.compose.widget.scaffold.SettingsScaffold
import org.uwuaosp.compose.widget.ui.SettingsTopIntro
import org.uwuaosp.settingsext.R
import org.uwuaosp.settingsext.SettingsExtTheme

class NavHandleDoubleTapSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsExtTheme {
                NavHandleDoubleTapSettingsScreen(lifecycle = lifecycle, onNavigateUp = ::finish)
            }
        }
    }
}

@Composable
private fun NavHandleDoubleTapSettingsScreen(lifecycle: Lifecycle, onNavigateUp: () -> Unit) {
    val context = LocalContext.current
    var enabled by remember {
        mutableStateOf(MomentSecureSettings.isNavHandleDoubleTapEnabled(context, true))
    }

    SettingsScaffold(
        title = stringResource(R.string.moment_nav_handle_double_tap_title),
        showBackButton = true,
        onNavigateUp = onNavigateUp,
        contentTopPadding = 0.dp,
    ) {
        SettingsTopIntro(
            text = stringResource(R.string.moment_nav_handle_double_tap_description),
            modifier = Modifier.padding(bottom = 16.dp),
        )
        NavHandleDoubleTapTutorialCard(lifecycle)
        MainSwitchPreference(
            title = stringResource(R.string.moment_nav_handle_double_tap_switch_title),
            checked = enabled,
            onCheckedChange = { newValue ->
                enabled = newValue
                MomentSecureSettings.setNavHandleDoubleTapEnabled(context, newValue)
            },
        )
    }
}
