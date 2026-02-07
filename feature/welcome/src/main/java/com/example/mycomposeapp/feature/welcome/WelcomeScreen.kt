package com.example.mycomposeapp.feature.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.core.ui.R as CoreUiR


@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = CoreUiR.drawable.app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spacing8),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = CoreUiR.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )


            Text(
                text = "AxisSolve",
                style = TextStyle(
                    brush = colors.goldTextGradient,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(spacing.spacing48))

            ButtonLarge(
                text = "Login",
                onClick = onLoginClick,
                style = ButtonStyle.Filled
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonLarge(
                text = "Register",
                onClick = onRegisterClick,
                style = ButtonStyle.Outlined
            )

            Spacer(modifier = Modifier.height(spacing.spacing24))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.textMuted
                )
                Text(
                    text = "OR",
                    color = colors.textMuted,
                    modifier = Modifier.padding(horizontal = spacing.spacing16),
                    fontSize = 14.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing24))

            ButtonLarge(
                text = "Continue with Google",
                onClick = onGoogleSignInClick,
                style = ButtonStyle.Social,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = CoreUiR.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(20.dp),
                        tint = colors.white
                    )
                }
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))

            Text(
                text = "By continuing, you agree to our Terms of Service",
                color = colors.textMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { onTermsClick() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    MyComposeAppTheme {
        WelcomeScreen(
            onLoginClick = {},
            onRegisterClick = {},
            onGoogleSignInClick = {},
            onTermsClick = {}
        )
    }
}
