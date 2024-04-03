package com.example.shopapplication.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.example.shopapplication.composables.ShopScreen
import com.example.shopapplication.content.ShopViewType
import com.example.shopapplication.state.ShopControlState
import com.example.shopapplication.state.ShopImpressionType
import com.example.shopapplication.state.ShopWishlistState
import com.example.shopapplication.viewModels.ShopViewModel
import com.example.shopapplication.viewModels.ShopWishlistViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

const val IS_WISHLIST = "isWishlist"
class ShopFragment : Fragment() {

    private val shopAdapter by cellAdapter()
    private val shopViewModel by sharedViewModel<ShopViewModel>()
    private val shopWishlistViewModel by sharedViewModel<ShopWishlistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        observeShopEvents()
        handleArguments()
    }

    private fun handleArguments() = shopViewModel.apply {
        when (arguments?.getBoolean(IS_WISHLIST)) {
            true -> {
                shopViewType = ShopViewType.WISHLIST
                shopWishlistViewModel.getWishlistedItems()
                emitControlState(ShopControlState.EmptyShopListView(isEnabled = false))
            }

            else -> {
                shopViewType = when (requireActivity().isLandScape()) {
                    true -> ShopViewType.LANDSCAPE
                    else -> ShopViewType.PORTRAIT
                }
                emitControlState(ShopControlState.EmptyWishlistView(isEnabled = false))
                getShopItems()
            }
        }.also { emitControlState(ShopControlState.UpdateTitle)  }
    }

    private fun observeShopEvents() {
        shopViewModel.shopItemsFlow.onEach {
            shopAdapter.setShopItems(it)
        }
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewScope)

        shopWishlistViewModel.shopWishlistControlStateFlow.onEach {
            when (it) {
                is ShopWishlistState.ShowToast -> shopViewModel.emitControlState(
                        ShopControlState.ShowToast(
                            show = true,
                            message = it.message
                        )
                    )
                is ShopWishlistState.EmptyWishlist -> shopViewModel.apply {
                    if (shopViewType == ShopViewType.WISHLIST) {
                        emitControlState(ShopControlState.EmptyWishlistView(isEnabled = true))
                    }
                }

                is ShopWishlistState.UpdateCellAtPosition -> shopAdapter.setItemAtPosition(it.position)
            }
        }
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewScope)

        shopWishlistViewModel.wishlistContentFlow
            .onEach {
                if(shopViewModel.shopViewType == ShopViewType.WISHLIST){
                    shopAdapter.setShopItems(it)
                }
            }
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewScope)
    }

    private fun setUpAdapter() = shopAdapter.apply {
        localCommunicator = { localEvent ->
            when (localEvent) {
                is LocalEvent.Wishlist -> wishlistItem(localEvent.cellId, localEvent.position)
                is LocalEvent.Buy -> viewScope.launch {
                    shopViewModel.apply {
                        handleImpression(
                            shopImpressionType = ShopImpressionType.URL_CLICK_IMPRESSION,
                            shopImpressionData = ShopImpressionData(cellId = localEvent.cellId)
                        )
                    }
                    deepLinkManager.router.openGenericWebView(url =localEvent.buyUrl, shouldShowBackButton = true, ignoreUrlParams = true)
                }
                else -> Unit
            }
        }
    }

    private fun wishlistItem(cellId: CellId, position : Int) = shopWishlistViewModel.apply {
        addToWishlist(
            position = position,
            cellId = cellId,
            cells = shopViewModel.shopItemsFlow.value
        )
        getWishlistCount()
        if (shopViewModel.shopViewType == ShopViewType.WISHLIST) getWishlistedItems()
    }

    override fun onResume() {
        super.onResume()
        shopViewModel.handleImpression(shopImpressionType = ShopImpressionType.PAGE_LOAD_IMPRESSION)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CompositionLocalProvider(LocalCellAdapter provides shopAdapter) {
                    ShopScreen(
                        shopUiState = shopViewModel.shopUiState.collectAsState().value,
                        onControlsStateChanged = shopViewModel::emitControlState
                    )
                }
            }
        }
    }
}
