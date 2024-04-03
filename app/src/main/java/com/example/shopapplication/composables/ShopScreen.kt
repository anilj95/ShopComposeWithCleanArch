package com.example.shopapplication.composables

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.example.shopapplication.R
import com.example.shopapplication.content.ShopScreenInfo
import com.example.shopapplication.state.ShopControlState
import com.example.shopapplication.state.ShopImpressionData
import com.example.shopapplication.state.ShopImpressionType
import com.example.shopapplication.state.ShopUiState
import com.example.shopapplication.utils.SHOP_BG_COLOR
import com.example.shopapplication.utils.TOAST_BACKGROUND
import com.example.shopapplication.utils.empty
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(
    shopUiState: ShopUiState,
    onControlsStateChanged: (ShopControlState) -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    if (shopUiState.showToast) {
        LaunchedEffect(key1 = Unit) {
            scope.launch {
                onControlsStateChanged(ShopControlState.ToastShown)
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    message = shopUiState.toastMessage,
                )
            }
        }
    }
    Scaffold(
        topBar = {
            ShopTitleView(
                title = shopUiState.title,
                durationInfo = shopUiState.durationInfo,
                itemSize = shopUiState.onScreenItemSize,
                isEmptyShopList = shopUiState.enableEmptyShopScreen,
                isEmptyWishList = shopUiState.enableEmptyWishlist,
                shopViewType = shopUiState.shopScreenInfo,
                onControlsStateChanged = onControlsStateChanged,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(8.dp)
            ) { data ->
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    content = { CustomToastMessage(message = data.message) },
                )
            }
        }
    ) {
        when {
            shopUiState.enableEmptyWishlist -> EmptyWishlistView(onControlsStateChanged = onControlsStateChanged)
            shopUiState.enableEmptyShopScreen -> EmptyShopItemView()

            else -> ShopItemsView(
                paddingValues = it,
                shopUiState = shopUiState,
                onControlsStateChanged = onControlsStateChanged
            )
        }
    }
}


@Composable
private fun EmptyWishlistView(onControlsStateChanged: (ShopControlState) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SHOP_BG_COLOR)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = EMPTY_WISHLIST_URL)
                    .apply(block = fun ImageRequest.Builder.() {
                        error(R.drawable.ic_background)
                        placeholder(R.drawable.ic_background)
                        scale(Scale.FIT)
                    }).build(),
            ),
            modifier = Modifier
                .padding(top = 50.dp)
                .width(170.dp)
                .height(120.dp)
                .align(Alignment.CenterHorizontally),
            contentDescription = String.empty,
        )
        Spacer(modifier = Modifier.height(20.dp))
        ShopText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 80.dp),
            textAlign = TextAlign.Center,
            text = AS_YOU_WATCH_TEXT,
            size = 12.sp,
            fontWeight = FontWeight.W400,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        ShopButton(onClick = {
            onControlsStateChanged.invoke(ShopControlState.CloseWishlistAndOpenShop)
        }, colors = ButtonDefaults.buttonColors(
            backgroundColor = EXPLORE_LOOKS_BUTTON_BG_COLOR
        ), modifier = Modifier.align(Alignment.CenterHorizontally)) {
            ShopText(
                text = SHOP_EXPLORE_LOOKS_TEXT,
                size = 12.sp,
                fontWeight = FontWeight.W500,
                lineHeight = 18.sp
            )
        }
    }

}

@Composable
private fun EmptyShopItemView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SHOP_BG_COLOR)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = EMPTY_SHOP_URL)
                    .apply(block = fun ImageRequest.Builder.() {
                        error(R.drawable.ic_background)
                        placeholder(R.drawable.ic_background)
                        scale(Scale.FIT)
                    }).build(),
            ),
            modifier = Modifier
                .padding(top = 50.dp)
                .width(170.dp)
                .height(120.dp)
                .align(Alignment.CenterHorizontally),
            contentDescription = String.empty,
        )
        Spacer(modifier = Modifier.height(20.dp))
        ShopText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 60.dp),
            textAlign = TextAlign.Center,
            text = SHOP_ITEM_EMPTY_TEXT,
            size = 16.sp,
            fontWeight = FontWeight.W700,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        ShopText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 40.dp),
            textAlign = TextAlign.Center,
            text = SHOP_ITEM_EXPLORE_EMPTY_TEXT,
            size = 12.sp,
            fontWeight = FontWeight.W500,
            lineHeight = 18.sp,
            color = SHOP_NO_NEW_PRODUCT_COLOR
        )
    }

}


@Composable
private fun ShopItemsView(
    paddingValues: PaddingValues,
    shopUiState: ShopUiState,
    onControlsStateChanged: (ShopControlState) -> Unit
) {
    Box(modifier = Modifier
        .background(SHOP_BG_COLOR)
        .fillMaxSize()
        .padding(paddingValues) ){
        val cellAdapter = LocalCellAdapter.current
        AndroidView(
            modifier = Modifier
                .addTestTag(SHOP_ITEMS_RECYCLER_VIEW),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    itemAnimator = null
                    isNestedScrollingEnabled = false
                    adapter = cellAdapter.create()
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                            layoutManager?.apply {
                                if (findFirstCompletelyVisibleItemPosition() >= 0 && findLastCompletelyVisibleItemPosition() >= 0) {
                                    onControlsStateChanged(
                                        ShopControlState.ShopImpression(
                                            shopImpressionType = ShopImpressionType.VISIBILITY_IMPRESSION,
                                            shopImpressionData = ShopImpressionData(
                                                firstVisible = findFirstVisibleItemPosition(),
                                                lastVisible = findLastCompletelyVisibleItemPosition()
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    })
                }
            }
        )
        when(shopUiState.showLoader){
            true -> ShowProgressBar()
            else -> Unit
        }
    }
}


@Composable
private fun ShopTitleView(
    title: String,
    durationInfo: String,
    itemSize : Int,
    isEmptyShopList : Boolean,
    isEmptyWishList : Boolean,
    shopViewType: ShopScreenInfo,
    onControlsStateChanged: (ShopControlState) -> Unit
) {
    val isLandScape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SHOP_BG_COLOR)
    ) {
        when(isLandScape){
            true -> LandScapeShopTitleView(onControlsStateChanged, title)
            else ->PortraitShopTitleView(onControlsStateChanged, title)
        }
        val shouldShowShopItems = shopViewType != ShopScreenInfo.Wishlist ||
                shopViewType == ShopScreenInfo.Wishlist && isEmptyWishList.not()

        if (isEmptyShopList.not() && shouldShowShopItems) {
            ShopDurationItemsUI(shopViewType, durationInfo, itemSize)

        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
private fun PortraitShopTitleView(
    onControlsStateChanged: (ShopControlState) -> Unit,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painterResource(
                id = R.drawable.ic_launcher_background
            ), contentDescription = String.empty,
            modifier = Modifier
                .padding(top = 8.dp, end = 16.dp)
                .align(Alignment.TopEnd)
                .clickable {
                    onControlsStateChanged(ShopControlState.Close)
                }
        )
    }

    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(
                id = R.drawable.ic_launcher_foreground
            ), contentDescription = String.empty
        )
        Spacer(modifier = Modifier.width(6.dp))
        ShopText(
            maxLines = 1,
            text = title,
            size = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.W700
        )

    }
}
@Composable
private fun LandScapeShopTitleView(
    onControlsStateChanged: (ShopControlState) -> Unit,
    title: String
) {

    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(
                id = androidx.core.R.drawable.ic_call_decline_low
            ), contentDescription = String.empty
        )
        Spacer(modifier = Modifier.width(6.dp))
        ShopText(
            maxLines = 1,
            text = title,
            size = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.W700
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painterResource(
                id = androidx.core.R.drawable.ic_call_decline
            ), contentDescription = String.empty,
            modifier = Modifier
                .padding(end = 4.dp)
                .clickable {
                    onControlsStateChanged(ShopControlState.Close)
                }
        )

    }
}

@Composable
internal fun ShowProgressBar() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {}) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = colorResource(id = R.color.purple_200),
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun CustomToastMessage(message: String) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.background(TOAST_BACKGROUND, shape = RoundedCornerShape(2.dp)),
        ){
            ShopText(text = message)
        }
    }
}

@Composable
private fun ShopDurationItemsUI(
    shopViewType: ShopScreenInfo,
    durationInfo: String,
    itemSize: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SHOP_BG_COLOR)
    ) {
        Row(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            ShopOutlinedButton(shape = RoundedCornerShape(4.dp),
                border = BorderStroke(width = 1.dp, color = SHOP_DURATION_OUTLINE_COLOR),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                onClick = { }) {

                Icon(
                    iconData = androidx.core.R.drawable.ic_call_answer_video,
                    modifier = Modifier
                        .padding(start = 2.dp, end = 4.dp),
                    size = 20.dp,
                    color = SHOP_DURATION_ICON_COLOR
                )
                Spacer(modifier = Modifier.width(4.dp))
                ShopText(
                    text = durationInfo,
                    size = 12.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    color = SHOP_DURATION_TEXT_COLOR
                )
            }

        }
        Spacer(modifier = Modifier.width(8.dp))
        when(shopViewType != ShopScreenInfo.Wishlist){
            true -> ShopItemExtraUI(itemSize)
            else -> Unit
        }


    }
}

@Composable
private fun ShopItemExtraUI(itemSize: Int) {
    ShopText(
        modifier = Modifier.padding(start = 12.dp),
        text = ON_SCREEN_TEXT,
        fontWeight = FontWeight.W500,
        lineHeight = 20.sp
    )
}