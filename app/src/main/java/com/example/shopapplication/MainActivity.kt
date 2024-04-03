package com.example.shopapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.os.bundleOf
import com.example.shopapplication.content.ShopViewType
import com.example.shopapplication.viewModels.ShopViewModel

class MainActivity : ComponentActivity() {

    private val shopViewModel by sharedViewModel<ShopViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showShopFragment()
    }

    private fun showShopFragment() {
        when (shopViewModel.shopViewType == ShopViewType.WISHLIST) {
            true -> {
                val shopFragment = ShopFragment().apply {
                    arguments = bundleOf(IS_WISHLIST to true)
                }
                childFragmentManager.commit(allowStateLoss = true) {
                    replace(
                        viewId,
                        shopFragment(),
                        SHOP_FRAGMENT_TAG
                    )
                }
            }

            else -> when (childFragmentManager.findFragmentByTag(SHOP_FRAGMENT_TAG) == null) {
                true -> childFragmentManager.commit(allowStateLoss = true) {
                    replace(
                        viewId,
                        ShopFragment(),
                        SHOP_FRAGMENT_TAG
                    )

                }
                else -> Unit
            }
        }
    }
}

