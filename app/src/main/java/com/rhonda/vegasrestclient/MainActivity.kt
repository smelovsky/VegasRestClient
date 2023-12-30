package com.rhonda.vegasrestclient

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhonda.vegasrestclient.ui.theme.VegasRestClientTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

lateinit var mainViewModel: MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            mainViewModel = hiltViewModel()

            if (mainViewModel.exitFromApp.value) {
                exitFromApp()
            }

            VegasRestClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }

    fun exitFromApp() {
        this.finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar (
            title = { Text("REST API Client") },
            actions = {
                IconButton(onClick = {
                    mainViewModel.exitFromApp.value = true
                }) {
                    Icon(
                        Icons.Outlined.ExitToApp,
                        contentDescription = "Exit",
                    )
                }
            }
        )
        },
        bottomBar = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                OutlinedButton(
                    onClick = {
                        mainViewModel.getSysAdmin()
                    }
                ) {
                    Text("Get sysadmin")
                }
            }

        },
    ) { innerPadding ->
        userListArea(innerPadding, modifier)
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun userListArea(innerPadding: PaddingValues, modifier: Modifier) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState: ScrollState = rememberScrollState()
    val showAlertDialog = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {

        if (showAlertDialog.value) {
            AlertDialog(
                onDismissRequest = {  },
                title = {
                    Row() {
                        androidx.compose.material3.Icon(
                            painterResource(R.drawable.vegas_08),
                            contentDescription = "AlertDialog",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(stringResource(R.string.app_name), fontSize = 22.sp)
                    }

                },
                text = { Text("Delete selected user?", fontSize = 16.sp) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mainViewModel.deleteSelectedItem()
                            showAlertDialog.value = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAlertDialog.value = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }

        Row() {
            LazyColumn (
                modifier = Modifier
                    .verticalScroll(state = scrollState, )
                    .height(LocalConfiguration.current.screenHeightDp.dp)
                    .fillMaxSize(),
            ) {
                itemsIndexed(
                    items = mainViewModel.userListBase,
                    key = { _, markerBaseItem -> markerBaseItem.id })
                { index, markerBaseItem ->
                    UserssEditBlock(
                        index = index,
                        item = markerBaseItem,
                        { showAlertDialog.value = true },
                    )

                }
            }
        }

        if (mainViewModel.showSysAdminInfo.value) {

            val height = LocalConfiguration.current.screenWidthDp.dp / 16 * 9

            Dialog(onDismissRequest = {mainViewModel.showSysAdminInfo.value = false }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding(4.dp)
                        .height(height)
                        .fillMaxWidth()
                        .clickable { }
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("${mainViewModel.name.value}")
                        Text("${mainViewModel.surname.value}")
                        Text("Phone: ${mainViewModel.phoneNumber.value}")
                    }

                }

            }
        }

        if (mainViewModel.showError.value) {

            val height = LocalConfiguration.current.screenWidthDp.dp / 16 * 9

            Dialog(onDismissRequest = {mainViewModel.showError.value = false }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding(4.dp)
                        .height(height)
                        .fillMaxWidth()
                        .clickable { }
                ) {
                    Text("Error: \"${mainViewModel.error.value}\"\n Response code: ${mainViewModel.responseCode.value}")
                }

            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@ExperimentalComposeUiApi
@Composable
fun UserssEditBlock(
    index: Int,
    item: UserBaseItem,
    showAlertDialog: ()-> Unit,
) {
    val id = item.id
    val details = mainViewModel.userListDetails[index]

    val name = details.name
    var isEditMode = details.isEditMode
    val isSelected = details.isSelected
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .combinedClickable(
                onLongClick = {
                    if (!mainViewModel.isEditMode()) {
                        mainViewModel.selectItemAndSetEditMode(index)
                    }
                },
                onDoubleClick = {
                    if (!mainViewModel.isEditMode()) {
                        mainViewModel.selectItemAndSetEditMode(index)
                    }
                },
                onClick = {
                    if (!mainViewModel.isEditMode()) {
                        mainViewModel.selectItem(index)
                    }
                },

                )

    ) {
        val focusRequester = remember { FocusRequester() }

        val (labelIconRef, titleRef, deleteIconRef, editIconRef, cancelIconRef, topDividerRef, bottomDividerRef) = createRefs()

        val dividerColor: Color = if (isEditMode) {
            MaterialTheme.colorScheme.secondary
        } else {
            if (isSelected) {
                MaterialTheme.colorScheme.secondary
            } else {
                Color.Transparent
            }
        }

        val dividerThickness = 1.5.dp
        Divider(
            color = dividerColor,
            thickness = dividerThickness,
            modifier = Modifier
                .constrainAs(topDividerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Divider(
            color = dividerColor,
            thickness = dividerThickness,
            modifier = Modifier
                .constrainAs(bottomDividerRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        androidx.compose.material3.Icon(
            painter = painterResource(R.drawable.ic_label),
            contentDescription = "Label",
            modifier = Modifier
                .constrainAs(labelIconRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, margin = 8.dp)
                }
        )


        if (isEditMode) {


            var textFieldValueOld: String by remember {
                mutableStateOf(name)
            }

            var textFieldValue: TextFieldValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = name,
                        selection = TextRange(name.length),
                        composition = TextRange(0, name.length)
                    )
                )
            }

            DisposableEffect(key1 = Unit) {
                focusRequester.requestFocus()
                onDispose { }
            }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .constrainAs(titleRef) {
                        width = Dimension.fillToConstraints
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(labelIconRef.end)
                        end.linkTo(deleteIconRef.start)
                    },

                value = textFieldValue,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        Log.d("zzz", "<${textFieldValue.text}>")
                        mainViewModel.renameItem(index, textFieldValue.text)
                    }
                ),
                onValueChange = { newValue -> textFieldValue = newValue },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.primary,
                ),


                )

            IconButton(
                onClick = { showAlertDialog() },
                modifier = Modifier
                    .constrainAs(deleteIconRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(titleRef.end)
                        end.linkTo(cancelIconRef.start)
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete",
                )
            }

            IconButton(
                onClick = { mainViewModel.renameItem(index, textFieldValueOld)
                },
                modifier = Modifier
                    .constrainAs(cancelIconRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(deleteIconRef.end)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cancel),
                    contentDescription = "Cancel",
                )
            }
        }
        else {

            if (isSelected) {

                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .constrainAs(titleRef) {
                            width = Dimension.fillToConstraints
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(labelIconRef.end, margin = 8.dp )
                            end.linkTo(editIconRef.start, )
                        }
                )

                IconButton(
                    onClick = {
                        mainViewModel.changeEditMode(index)
                    },
                    modifier = Modifier
                        .constrainAs(editIconRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(titleRef.end, margin = 8.dp )
                            end.linkTo(parent.end, margin = 8.dp)
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                    )

                }
            } else {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .constrainAs(titleRef) {
                            width = Dimension.fillToConstraints
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(labelIconRef.end, margin = 8.dp )
                            end.linkTo(parent.end, )
                        }
                )
            }
        }
    }
}

