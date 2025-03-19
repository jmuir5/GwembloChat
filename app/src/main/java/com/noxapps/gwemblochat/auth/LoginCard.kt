package com.noxapps.familygiftlist.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.User

@Composable
fun LoginCard(
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    textIconColors: Color? = null,
    loginState: MutableState<Boolean>,
    loggedUser: MutableState<User>,
    viewModel: LoginViewModel
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var enabled = remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var visibilityDetails =
        if(passwordVisible){
            Pair(R.drawable.visibility_24px, "Password is visible")
        }else{
            Pair(R.drawable.visibility_off_24px, "Password is not visible")
        }

    val (emailFocReq, pwFocReq, buttonFocReq) = remember { FocusRequester.createRefs() }

    var emptyCheck by remember { mutableStateOf(false) }

    val emailMalformedError by remember {
        derivedStateOf { email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    }
    val emailEmptyError by remember {
        derivedStateOf { email.isEmpty() && emptyCheck }
    }
    val passwordEmptyError by remember {
        derivedStateOf { password.isEmpty() && emptyCheck }
    }
    val passwordMalformedError by remember { derivedStateOf { password.isNotEmpty() && !password.isValidPassword() } }

    val emailLabelString =
        if (emailEmptyError) "Email can not be empty"
        else if(emailMalformedError) "Email invalid"
        else "Email"

    val passwordLabelString =
        if (passwordEmptyError) "Password can not be empty"
        else if(passwordMalformedError) "Password invalid"
        else "Password"


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        //email
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocReq),
                /*.autofill(autofillTypes = listOf(AutofillType.EmailAddress)) {
                    //if(email.isEmpty())pwFocReq.requestFocus()
                    email = it
                },

                 */
            colors = textFieldColors,
            value = email,
            onValueChange = { email = it.replace(" ", "") },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.mail_24px),
                    contentDescription = "Email",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    pwFocReq.requestFocus()
                }
            ),
            label = { Text(emailLabelString) },
            isError = (emailEmptyError || emailMalformedError)

        )
        //Password
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(pwFocReq)
                ,/*.autofill(autofillTypes = listOf(AutofillType.Password)) {
                    password = it
                },
                */
            value = password,
            onValueChange = { password = it.replace(" ", "") },
            colors = textFieldColors,
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.key_24px),
                    contentDescription = "password",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            trailingIcon = {
                Image(
                    modifier = Modifier
                        .clickable {
                            passwordVisible = !passwordVisible
                        },
                    painter = painterResource(id = visibilityDetails.first),
                    contentDescription = visibilityDetails.second,
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            visualTransformation =
            if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                autoCorrectEnabled = false
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    buttonFocReq.requestFocus()
                }
            ),
            label = { Text(passwordLabelString) },
            isError = (passwordEmptyError || passwordMalformedError)

        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(buttonFocReq)
                .onFocusChanged {
                    if (it.isFocused) {
                        emptyCheck = true
                        if ((emailMalformedError || emailEmptyError || passwordEmptyError ||
                                    passwordMalformedError)
                        ) {
                            Toast.makeText(
                                context,
                                "Please correct the above errors",
                                Toast.LENGTH_SHORT,
                            )
                                .show()
                        } else {
                            viewModel.login(
                                email,
                                password,
                                enabled,
                                loggedUser,
                                context,
                                coroutineScope
                            )
                        }
                    }
                }
                .focusable(),
            shape = RectangleShape,

            onClick = {
                emptyCheck = true
                if ((emailMalformedError || emailEmptyError || passwordEmptyError ||
                            passwordMalformedError)
                ) {
                    Toast.makeText(
                        context,
                        "Please correct the above errors",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    viewModel.login(
                        email,
                        password,
                        enabled,
                        loggedUser,
                        context,
                        coroutineScope
                    )
                }
            }
        ) {
            Text("Login")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),

                text = "New Here?"
            )
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RectangleShape,
                onClick = { loginState.value = !loginState.value }) {
                Text("Create Account")
            }
        }
    }
}