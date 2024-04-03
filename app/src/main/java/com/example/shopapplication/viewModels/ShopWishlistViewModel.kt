package com.example.shopapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopapplication.state.ShopBannerUiState
import com.example.shopapplication.state.ShopWishlistState
import com.example.shopapplication.usecases.ShopUseCase
import com.example.shopapplication.utils.updateValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

internal class ShopWishlistViewModel(
    private val shopUseCase: ShopUseCase
) : ViewModel() {

    private val _wishlistContentFlow = MutableStateFlow<List<CellItem>>(emptyList())
    val wishlistContentFlow: StateFlow<List<CellItem>>
        get() = _wishlistContentFlow.asStateFlow()

    private val _shopBannerUiState = MutableStateFlow(ShopBannerUiState())
    val shopBannerUiState: StateFlow<ShopBannerUiState> get() = _shopBannerUiState.asStateFlow()

    private val _shopWishlistControlStateFlow = MutableSharedFlow<ShopWishlistState>()
    val shopWishlistControlStateFlow = _shopWishlistControlStateFlow.asSharedFlow()


    fun getWishlistedItems() = viewModelScope.launch {
        shopUseCase.execute(
            ShopUseCase.Input(
                operationType = ShopUseCase.OperationType.GET_WISHLISTED_ITEMS,
            )
        ).onSuccess { output -> handleResponse(output) }.onFailure { Timber.e(it) }
    }

    fun getWishlistCount() = viewModelScope.launch {
        shopUseCase.execute(
            ShopUseCase.Input(
                operationType = ShopUseCase.OperationType.GET_WISHLIST_COUNT,
            )
        ).onSuccess { output -> handleResponse(output) }.onFailure { Timber.e(it) }
    }

    private fun handleResponse(output: ShopUseCase.Output) {
        when (output) {
            is ShopUseCase.Output.WishlistedItems -> output.railItem.firstOrNull()?.cells?.let { cells ->
                _wishlistContentFlow.value = cells
            }
            is ShopUseCase.Output.WishlistCount -> when (output.count > 0) {
                    true -> viewModelScope.launch {
                        _shopBannerUiState.updateValue {
                            copy(
                                enableWishlist = true,
                                wishlistCountText = WISHLIST_BUTTON_TEXT,

                            )
                        }
                    }

                    else -> viewModelScope.launch {
                        _shopWishlistControlStateFlow.emit(ShopWishlistState.EmptyWishlist)
                        _shopBannerUiState.updateValue { copy(enableWishlist = false) }
                    }
                }
            else -> Unit
        }
    }

    fun disableWishlist() = viewModelScope.launch {
        _shopBannerUiState.updateValue { copy(enableWishlist = false) }
    }

    fun updateBannerText(assetType: AssetType) = viewModelScope.launch {
        when (assetType) {
            AssetType.EPISODE -> _shopBannerUiState.updateValue {
                copy(
                    bannerText = OR_EXPLORE_MORE_WISHLIST_TEXT,
                    )
            }

            else -> _shopBannerUiState.updateValue {
                copy(
                    bannerText = OR_EXPLORE_MORE_WISHLIST_MOVIE_TEXT,

                )
            }
        }

    }

    fun addToWishlist(position: Int, cellId: CellId, cells: List<CellItem>) = viewModelScope.launch {
        cells.filter { cellItem -> cellItem.id.toCellId() == cellId }
            .map { cellItem ->
                when (cellItem.isFavorite) {
                    true -> _shopWishlistControlStateFlow.emit(
                        ShopWishlistState.ShowToast(
                            message = REMOVE_FROM_WISHLIST_TOAST,
                        )
                    )

                    false -> _shopWishlistControlStateFlow.emit(
                        ShopWishlistState.ShowToast(
                            message = ADD_TO_WISHLIST_TOAST,
                            )
                    )
                }
                cellItem.isFavorite = cellItem.isFavorite.not()
                _shopWishlistControlStateFlow.emit(ShopWishlistState.UpdateCellAtPosition(position = position))
            }
    }

}