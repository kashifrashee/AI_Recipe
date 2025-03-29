package com.example.airecipe.ui_components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.airecipe.R
import com.example.airecipe.ui.theme.ButtonColor

@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        isError = isError,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ButtonColor,
            unfocusedBorderColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(20.dp)
    )
}


@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,  // ✅ Use content lambda for flexibility
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor
        ),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content() // ✅ Pass content inside the button
        }
    }
}

@Composable
fun SocialAuthButtons(
    modifier: Modifier = Modifier,
    onGoogleClick: () -> Unit,
    onMobileClick: () -> Unit
) {
    Log.d("SocialAuthButtons", "Composable Initialized")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Google Sign-In Button
        SocialButton(
            iconResId = R.drawable.icons8_google_48,
            contentDescription = "Google",
            onClick = {
                //Log.d("GoogleSignIn", "Google Button Clicked in SocialAuthButtons!")
                onGoogleClick()
            }
        )

        // Facebook Sign-In Button
        SocialButton(
            iconResId = R.drawable.icons8_mobile_phone_50,
            contentDescription = "Mobile",
            onClick = {
                //Log.d("MobileSignIn", "Mobile Button Clicked in SocialAuthButtons!")
                onMobileClick()
            }
        )
    }
}

@Composable
fun SocialButton(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F4F4)), // Background Color
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .clickable {
                Log.d("ButtonClick", "$contentDescription Card Clicked!")
                onClick()
            }
            .size(width = 80.dp, height = 56.dp) // Ensure uniform button size
    ) {
        IconButton(
            onClick = {
                Log.d("ButtonClick", "$contentDescription IconButton Clicked!")
                onClick()
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription,
                tint = Color.Unspecified // Keep original icon colors
            )
        }
    }
}


@Composable
fun NavigationAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.back_arrow),
            contentDescription = "Back",
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    onBackClick()
                }
        )

        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.icons8_allrecipes_200),
            contentDescription = "App Icon",
            modifier = Modifier
                .size(50.dp)
        )
    }
}


@Composable
fun OtpTextField(modifier: Modifier = Modifier, otp: String, onOtpChange: (String) -> Unit) {
    BasicTextField(
        value = otp,
        onValueChange = {
            if (it.length <= 6) {
                Log.d("OtpTextField", "New OTP value: $it")
                onOtpChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (otp.length > index) {
                            Text(
                                text = otp[index].toString(),
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun TermsAndConditionsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { /* Prevent dialog from closing on outside tap */ }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Terms & Conditions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TermsAndConditionsContent()

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor
                    ),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text("I Accept")
                }
            }
        }
    }
}

@Composable
private fun TermsAndConditionsContent() {
    val terms = listOf(
        "Acceptance of Terms" to "By using the AIRecipe app, you agree to comply with and be bound by these Terms & Conditions. If you do not agree, please do not use the app.",
        "Use of the App" to "👉 The app provides food recipes for informational and personal use only.\n" +
                "👉 Users are responsible for ensuring the ingredients used are suitable for their dietary needs.\n" +
                "👉 The app is not a substitute for professional nutritional or health advice.",
        "User Responsibilities" to "👉 The app is intended for lawful use only; any misuse may result in access restrictions.",
        "Recipe Accuracy" to "👉 While we strive for accuracy, we do not guarantee that all recipes are error-free.\n" +
                "👉 Users should verify measurements and cooking times before following any recipe.",
        "Limitation of Liability" to "👉 AIRecipe is not responsible for any health-related issues, allergic reactions, or cooking mishaps that may arise from using the app.\n" +
                "👉 The app and its content are provided \"as is\" without warranties of any kind.",
        "Changes to Terms" to "We reserve the right to modify these terms at any time. Continued use of the app after changes indicates acceptance.",
        "Contact Us" to "If you have any questions about these Terms & Conditions, please reach out through the app’s support section."
    )

    Column {
        terms.forEach { (title, description) ->
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}


/*
@Preview
@Composable
private fun NavigationAppBarPreview() {
    NavigationAppBar(
        onBackClick = {}
    )
}
*/

/*

@Preview
@Composable
private fun SocialButtons() {
    SocialAuthButtons(
        onGoogleClick = { */
/* Handle Google Sign-In *//*
 },
        onMobileClick = {}
    )
}

@Preview
@Composable
fun PreviewsTextField() {
    CustomOutlinedTextField(
        value = "kashif@gmail.com",
        onValueChange = {},
        label = "Email",
        isError = false,
        modifier = Modifier
            .background(Color.Transparent, RoundedCornerShape(25.dp))
    )
} */

@Preview
@Composable
fun PreviewsButton() {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor
        ),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier

    ) {
        Text("I Accept")
    }
}
