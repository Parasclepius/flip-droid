package com.flipperdevices.info.impl.compose.bar

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.info.impl.R

private const val EMPTY_BATTERY = 0f
private const val FIRST_BATTERY_THRESHOLD = 0.15f
private const val SECOND_BATTERY_THRESHOLD = 0.4f
private const val FULL_BATTERY = 1.0f

// batteryBackground/batteryCharging stay on LocalPallet: they're theme-invariant grays drawn
// against the battery icon's own hardcoded white fill (see Color.White below), not the app
// background. FlipperPalletV2 has no theme-invariant token for this exact gray - every
// candidate (icon.neutral tiers, etc.) is theme-adaptive and would shift the icon's look
// between light/dark, which the current design deliberately avoids.
@Composable
fun ComposableFlipperBattery(
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) percent: Float,
    isCharging: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            BatteryContent(percent)
            if (isCharging) {
                Icon(
                    painter = painterResource(R.drawable.ic_charging),
                    contentDescription = null,
                    tint = LocalPallet.current.batteryCharging
                )
            }
        }
        Icon(
            modifier = Modifier.padding(start = 1.dp),
            painter = painterResource(R.drawable.ic_battery_pin),
            contentDescription = null,
            tint = LocalPallet.current.batteryBackground
        )
    }
}

@Composable
private fun BatteryContent(
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) percent: Float,
    modifier: Modifier = Modifier
) {
    val batteryColor = when (percent) {
        in EMPTY_BATTERY..FIRST_BATTERY_THRESHOLD -> LocalPalletV2.current.illustration.danger.primary
        in FIRST_BATTERY_THRESHOLD..SECOND_BATTERY_THRESHOLD -> LocalPalletV2.current.illustration.warning.primary
        in SECOND_BATTERY_THRESHOLD..FULL_BATTERY -> LocalPalletV2.current.illustration.success.primary
        else -> LocalPalletV2.current.illustration.danger.primary
    }

    Row(
        modifier
            .clip(RoundedCornerShape(3.dp))
            .background(LocalPallet.current.batteryBackground)
            .padding(1.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White)
            .padding(1.66.dp)
            .clip(RoundedCornerShape(1.dp))
    ) {
        val remainingWeight = 1.0f - percent
        Box(
            Modifier
                .weight(weight = percent)
                .fillMaxHeight()
                .background(batteryColor)
        )
        if (remainingWeight > 0f) {
            Box(Modifier.weight(remainingWeight))
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFlipperBatteryPreview() {
    Box {
        ComposableFlipperBattery(
            percent = 1.0f,
            isCharging = true,
            Modifier.size(width = 30.dp, height = 14.dp),
        )
    }
}
