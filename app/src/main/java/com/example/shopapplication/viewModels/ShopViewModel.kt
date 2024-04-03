package com.example.shopapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopapplication.content.ShopScreenInfo
import com.example.shopapplication.content.ShopViewType
import com.example.shopapplication.state.ShopControlState
import com.example.shopapplication.state.ShopImpressionData
import com.example.shopapplication.state.ShopImpressionType
import com.example.shopapplication.state.ShopUiState
import com.example.shopapplication.usecases.ShopUseCase
import com.example.shopapplication.utils.empty
import com.example.shopapplication.utils.updateValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

internal class ShopViewModel(
    private val shopUseCase: ShopUseCase,
) : ViewModel() {

    private val _shopItemsFlow = MutableStateFlow<List<CellItem>>(emptyList())
    val shopItemsFlow: StateFlow<List<CellItem>>
        get() = _shopItemsFlow.asStateFlow()

    private val _shopControlStateFlow = MutableSharedFlow<ShopControlState>()
    val shopControlStateFlow = _shopControlStateFlow.asSharedFlow()

    private val _shopUiState = MutableStateFlow(ShopUiState())
    val shopUiState: StateFlow<ShopUiState> get() = _shopUiState.asStateFlow()

    private var timeStampList = mutableListOf<Int>()
    private var previousPlayerDuration = 0
    var shopViewType = ShopViewType.BACKGROUND
    private lateinit var videoRefId : String
    private var firstVisibleCalled = 0
    private var lastVisibleCalled = 0

    init {
        shopControlStateFlow.onEach { state ->
            when (state) {
                is ShopControlState.ToastShown -> _shopUiState.updateValue {
                    copy(
                        showToast = false,
                        toastMessage = String.empty
                    )
                }

                is ShopControlState.EmptyWishlistView -> _shopUiState.updateValue {
                    copy(enableEmptyWishlist = state.isEnabled)
                }

                is ShopControlState.EmptyShopListView -> _shopUiState.updateValue {
                    copy(enableEmptyShopScreen = state.isEnabled)
                }

                is ShopControlState.UpdateItemCount -> _shopUiState.updateValue {
                    copy(onScreenItemSize = state.count)
                }

                is ShopControlState.ShopImpression -> handleImpression(
                    shopImpressionType = state.shopImpressionType,
                    shopImpressionData = state.shopImpressionData
                )
                is ShopControlState.ShowToast-> {
                    _shopUiState.updateValue {
                        copy(
                            showToast = state.show,
                            toastMessage = state.message
                        )
                    }
                }
                is ShopControlState.UpdateTitle -> when (shopViewType) {
                    ShopViewType.WISHLIST -> _shopUiState.updateValue {
                        copy(
                            title = WISHLIST_BUTTON_TEXT,
                            durationInfo = WISHLIST_ITEM_DURATION_INFO_TEXT,
                            shopScreenInfo = ShopScreenInfo.Wishlist,
                        )
                    }
                    else -> _shopUiState.updateValue {
                        copy(
                            title = SHOP_EXPLORE_LOOKS_TEXT,
                            durationInfo = SHOP_ITEM_DURATION_INFO_TEXT,
                            shopScreenInfo = ShopScreenInfo.Shop,
                        )
                    }
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }


    fun getShopItemTimestamps(assetId: ContentId) = viewModelScope.launch {
        _shopUiState.updateValue { copy(showLoader = true) }

            when(assetId.type == ContentId.Type.MOVIE || assetId.type == ContentId.Type.EPISODE || assetId.type == ContentId.Type.SHOW){
                true ->{
                    emitControlState(ShopControlState.ShopBannerVisibility(isVisible = true))
                    emitControlState(ShopControlState.EmptyShopListView(isEnabled = true))
                }
                else -> Unit
            }
            shopUseCase.execute(
                ShopUseCase.Input(
                    operationType = ShopUseCase.OperationType.GET_SHOP_TIMESTAMPS,
                    assetId = assetId.toString()
                )
            ).onSuccess {
                _shopUiState.updateValue { copy(showLoader = false) }
                handleResponse(it)
            }.onFailure {
                _shopUiState.updateValue { copy(showLoader = false) }
                Timber.e(it)
            }


    }

    fun emitControlState(shopControlState: ShopControlState) =
        viewModelScope.launch { _shopControlStateFlow.emit(shopControlState) }

    fun handlePlayerDurationUpdate(timeStamp: Int) = viewModelScope.launch {
        if (previousPlayerDuration != timeStamp) {
            previousPlayerDuration = timeStamp
            when (timeStampList.isNotEmpty() && timeStampList.contains(timeStamp)) {
                true -> shopUseCase.execute(
                    ShopUseCase.Input(operationType = ShopUseCase.OperationType.GET_CACHED_TIMESTAMPS)
                ).onSuccess {
                    handleResponse(it, timeStamp)
                }
                else -> Unit
            }
        }
    }

    fun getShopItems(timeStamp: Int = 0) = viewModelScope.launch {
        if (shopViewType != ShopViewType.WISHLIST) _shopUiState.updateValue { copy(showLoader = true) }
        shopUseCase.execute(
            ShopUseCase.Input(
                operationType = ShopUseCase.OperationType.GET_SHOP_ITEMS,
                shopViewType = shopViewType,
                videoRefId = when (this@ShopViewModel::videoRefId.isInitialized) {
                    true -> videoRefId
                    else -> String.empty
                },
                timeStamp = timeStamp
            )
        ).onSuccess { output ->
            _shopUiState.updateValue { copy(showLoader = false) }
            handleResponse(output, timeStamp)
        }.onFailure {
            _shopUiState.updateValue { copy(showLoader = false) }
            Timber.e(it)
        }
    }

    private fun handleResponse(output: ShopUseCase.Output, timeStamp: Int = ZERO) {
        when (output) {
            is ShopUseCase.Output.ShopItems -> viewModelScope.launch {
                if (output.railItem.cells.isNotEmpty()) {
                    emitControlState(ShopControlState.EmptyShopListView(isEnabled = false))
                    emitControlState(ShopControlState.ShopIconVisibility(showTooltip = false, showIcon = true, tooltipText = String.empty))
                    if (shopViewType != ShopViewType.WISHLIST && shopViewType != ShopViewType.BACKGROUND) {
                        _shopItemsFlow.emit(output.railItem.cells)
                        emitControlState(ShopControlState.UpdateItemCount(output.railItem.cells.size))
                    }
                }
            }

            is ShopUseCase.Output.ShopTimeStamps -> viewModelScope.launch {
                if (timeStampList.isEmpty()) {
                    emitControlState(
                        ShopControlState.ShopIconVisibility(
                            showTooltip = true,
                            showIcon = true,
                            tooltipText = SHOP_TOOLTIP_TEXT

                        )
                    )
                }
                videoRefId = output.videoReferenceId.orEmpty()
                timeStampList = output.timeStamps.toMutableList()
            }

            is ShopUseCase.Output.CachedTimeStamps -> when (output.timeStamps.contains(timeStamp.toString())
                .not()) {
                true -> getShopItems(timeStamp = timeStamp)
                else -> Unit
            }

            else -> Unit
        }
    }

    fun handleImpression(
        shopImpressionType: ShopImpressionType,
        shopImpressionData: ShopImpressionData = ShopImpressionData()
    ) = viewModelScope.launch {
        when (shopImpressionType) {
            ShopImpressionType.VISIBILITY_IMPRESSION -> shopImpressionData.firstVisible?.let { firstVisible ->
                shopImpressionData.lastVisible?.let { lastVisible ->
                    if (shopViewType != ShopViewType.WISHLIST
                        && (firstVisible == firstVisibleCalled && lastVisible == lastVisibleCalled).not()
                    ) {
                        firstVisibleCalled = shopImpressionData.firstVisible
                        lastVisibleCalled = shopImpressionData.lastVisible
                        for (index in shopImpressionData.firstVisible..shopImpressionData.lastVisible) {
                            shopItemsFlow.value[index].apply {
                                visibilityFeedbackUrl?.let { url ->
                                    shopUseCase.execute(
                                        ShopUseCase.Input(
                                            operationType = ShopUseCase.OperationType.SEND_IMPRESSION,
                                            url = url.plus(TID).plus(impressionToken)
                                        )
                                    )
                                }
                            }
                        }
                    }

                }
            }

            ShopImpressionType.PAGE_LOAD_IMPRESSION -> shopItemsFlow.value.distinctBy { it.pageLoadPingUrl }.map { cellItem ->
                cellItem.pageLoadPingUrl?.let { shopUseCase.execute(
                    ShopUseCase.Input(
                        operationType = ShopUseCase.OperationType.SEND_IMPRESSION,
                        url = it
                    )
                ) }
            }

            ShopImpressionType.URL_CLICK_IMPRESSION -> shopItemsFlow.value.filter { cellItem -> cellItem.id.toCellId() == shopImpressionData.cellId }
                .map {
                    it.pingUrlBase?.let { pingUrlBase ->
                        shopUseCase.execute(
                            ShopUseCase.Input(
                                operationType = ShopUseCase.OperationType.SEND_IMPRESSION,
                                url = pingUrlBase.plus(it.urlPingSuffix)
                            )
                        )
                    }
                }
        }
    }

    fun reset() = viewModelScope.launch {
            emitControlState(
                ShopControlState.ShopIconVisibility(
                    showIcon = false,
                    showTooltip = false,
                    tooltipText = String.empty
                )
            )
            emitControlState(ShopControlState.Close)
            emitControlState(ShopControlState.ShopBannerVisibility(isVisible = false))
            shopUseCase.execute(ShopUseCase.Input(operationType = ShopUseCase.OperationType.CLEAR_DATA))
            emitControlState(ShopControlState.DisableWishlist)
            previousPlayerDuration = ZERO
            timeStampList.clear()

    }

}