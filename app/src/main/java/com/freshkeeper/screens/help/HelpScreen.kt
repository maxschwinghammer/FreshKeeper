package com.freshkeeper.screens.help

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun HelpScreen(navController: NavHostController) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    val faqList =
        listOf(
            stringResource(R.string.faq_question_1) to stringResource(R.string.faq_answer_1),
            stringResource(R.string.faq_question_2) to stringResource(R.string.faq_answer_2),
            stringResource(R.string.faq_question_4) to stringResource(R.string.faq_answer_4),
            stringResource(R.string.faq_question_5) to stringResource(R.string.faq_answer_5),
            stringResource(R.string.faq_question_6) to stringResource(R.string.faq_answer_6),
            stringResource(R.string.faq_question_7) to stringResource(R.string.faq_answer_7),
        )

    val listState = rememberLazyListState()
    val showUpperTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val showLowerTransition by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.size < faqList.size
        }
    }

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 3, navController, notificationsViewModel)
                }
            },
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.faq),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                    )
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        itemsIndexed(faqList) { index, (question, answer) ->
                            FAQAccordion(
                                question = question,
                                answer = answer,
                                isExpanded = expandedIndex == index,
                                onClick = {
                                    expandedIndex = if (expandedIndex == index) null else index
                                },
                            )
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (showUpperTransition) {
                            UpperTransition()
                        }
                        if (showLowerTransition) {
                            LowerTransition(modifier = Modifier.align(Alignment.BottomCenter))
                        }
                    }

                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(ComponentBackgroundColor, RoundedCornerShape(10.dp))
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.answer_not_found),
                            color = WhiteColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Button(
                            onClick = { navController.navigate("contact") },
                            modifier =
                                Modifier.fillMaxWidth().padding(5.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
                        ) {
                            Text(
                                text = stringResource(R.string.send_us_message),
                                color = GreyColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}
