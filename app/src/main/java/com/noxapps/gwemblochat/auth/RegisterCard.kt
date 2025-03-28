package com.noxapps.gwemblochat.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.noxapps.gwemblochat.R
import com.noxapps.gwemblochat.data.User
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RegisterCard(
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    textIconColors: Color? = null,
    registerState: MutableState<Boolean>,
    loggedUser: MutableState<User>,
    viewModel: LoginViewModel
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("") }
    /*var lastName by remember { mutableStateOf("") }
    var birthdayState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = PastOrPresentSelectableDates
    )
    var birthdayDialogueState by remember { mutableStateOf(false) }*/
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var enabled = remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    var emptyCheck by remember { mutableStateOf(false) }

    val fNameError by remember {
        derivedStateOf { userName.isEmpty() && emptyCheck }
    }
    /*val lNameError by remember {
        derivedStateOf { lastName.isEmpty() && emptyCheck }
    }
    val birthdayError by remember {
        derivedStateOf { birthdayState.selectedDateMillis == null && emptyCheck }
    }*/
    val emailMalformedError by remember {
        derivedStateOf { email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    }
    val emailEmptyError by remember {
        derivedStateOf { email.isEmpty() && emptyCheck }
    }
    val passwordEmptyError by remember {
        derivedStateOf { password.isEmpty() && emptyCheck }
    }
    val confirmPasswordEmptyError by remember {
        derivedStateOf { confirmPassword.isEmpty() && emptyCheck }
    }

    val passwordMalformedError by remember { derivedStateOf { password.isNotEmpty() && !password.isValidPassword() } }
    val passwordsMatchError by remember { derivedStateOf { (password != confirmPassword) } }


    val emailLabelString =
        if (emailEmptyError) "Email can not be empty"
        else if(emailMalformedError) "Email invalid"
        else "Email"

    val passwordLabelString =
        if (passwordEmptyError) "Password can not be empty"
        else if(passwordMalformedError) "Password invalid"
        else "Password"

    val confirmPasswordLabelString =
        if (confirmPasswordEmptyError) "Password can not be empty"
        else if(passwordsMatchError) "Passwords do not match"
        else "Confirm Password"




    val visibilityDetails =
        if(passwordVisible){
            Pair(R.drawable.visibility_24px, "Password is visible")
        }else{
            Pair(R.drawable.visibility_off_24px, "Password is not visible")
        }

    val (firstNameFocReq, emailFocReq, pwFocReq, confFocReq, buttonFocReq) =
        remember { FocusRequester.createRefs() }

    val zoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        //First name
        TextField(
            modifier = Modifier
                .padding(0.dp,4.dp)
                .fillMaxWidth()
                .focusRequester(firstNameFocReq)
                ,/*.autofill(autofillTypes = listOf(AutofillType.PersonFirstName)) {
                    if (firstName.isEmpty()) bDayFocReq.requestFocus()
                    firstName = it
                },*/
            colors = textFieldColors,
            value = userName,
            onValueChange = { if (it.length < 32) userName = it },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.person_24px),
                    contentDescription = "User Name",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    emailFocReq.requestFocus()
                }
            ),
            label = {
                if (fNameError)
                    Text("Name can not be empty")
                else
                    Text("User Name")
            },
            isError = fNameError
        )
        //email
        TextField(
            modifier = Modifier
                .padding(0.dp,4.dp)
                .fillMaxWidth()
                .focusRequester(emailFocReq)
                ,/*.autofill(autofillTypes = listOf(AutofillType.EmailAddress)) {
                    if (email.isEmpty()) pwFocReq.requestFocus()
                    email = it
                },*/
            colors = textFieldColors,
            value = email,
            onValueChange = { email = it.replace(" ", "") },
            //placeholder = { Text("Email") },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.mail_24px),
                    contentDescription = "Email",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
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
                .padding(0.dp,4.dp)
                .fillMaxWidth()
                .focusRequester(pwFocReq)
                ,/*.autofill(autofillTypes = listOf(AutofillType.NewPassword)) {
                    password = it
                    confirmPassword = it
                },*/
            value = password,
            onValueChange = { password = it.replace(" ", "") },
            colors = textFieldColors,
            placeholder = { Text("Password") },
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
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                autoCorrectEnabled = false
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    confFocReq.requestFocus()
                }
            ),
            label = { Text(passwordLabelString) },
            isError = (passwordEmptyError || passwordMalformedError)


        )
        //Confirm Password
        TextField(
            modifier = Modifier
                .padding(0.dp,4.dp)
                .fillMaxWidth()
                .focusRequester(confFocReq)
                ,/*.autofill(autofillTypes = listOf(AutofillType.NewPassword)) {
                    password = it
                    confirmPassword = it
                },*/
            value = confirmPassword,
            onValueChange = { confirmPassword = it.replace(" ", "") },
            colors = textFieldColors,
            //placeholder = { Text("Confirm Password") },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.key_24px),
                    contentDescription = "Confirm Password",
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
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                autoCorrectEnabled = false
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    buttonFocReq.requestFocus()
                }
            ),
            isError = (confirmPasswordEmptyError || passwordsMatchError),
            label = {
                Text(confirmPasswordLabelString)
            }


        )
        Button(
            modifier = Modifier
                .padding(0.dp,4.dp)
                .fillMaxWidth()
                .focusRequester(buttonFocReq)
                .onFocusChanged {
                    if (it.isFocused) {
                        emptyCheck = true
                        if ((fNameError || passwordsMatchError ||
                                    passwordMalformedError || emailMalformedError || emailEmptyError ||
                                    passwordEmptyError || confirmPasswordEmptyError)
                        ) {
                            Toast
                                .makeText(
                                    context,
                                    "Please correct the above errors",
                                    Toast.LENGTH_SHORT,
                                )
                                .show()
                        } else {
                            viewModel.register(
                                userName = userName,
                                email = email,
                                password = password,
                                enableState = enabled,
                                loggedUser = loggedUser,
                                context = context,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                }
                .focusable(),
            shape = RoundedCornerShape(20.dp),

            onClick = {
                emptyCheck = true
                //for some reason putting all the errors into a list and checking with
                // list.contains(true) didnt work. i actually hate this but it works.
                if ((fNameError || passwordsMatchError ||
                            passwordMalformedError || emailMalformedError || emailEmptyError ||
                            passwordEmptyError || confirmPasswordEmptyError)
                ) {
                    Toast.makeText(
                        context,
                        "Please correct the above errors",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    viewModel.register(
                        userName = userName,
                        email = email,
                        password = password,
                        enableState = enabled,
                        loggedUser = loggedUser,
                        context = context,
                        coroutineScope = coroutineScope
                    )
                }
            }) {
            Text("Create Account")
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

                text = "Got an account?"
            )
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                onClick = { registerState.value = !registerState.value }) {
                Text("Login")
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates: SelectableDates {
    @ExperimentalMaterial3Api
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= System.currentTimeMillis()
    }

    @ExperimentalMaterial3Api
    override fun isSelectableYear(year: Int): Boolean {
        return year <= LocalDate.now().year
    }
}