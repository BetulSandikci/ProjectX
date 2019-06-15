package co.icanteach.projectx.ui

import co.icanteach.projectx.common.Status
import co.icanteach.projectx.data.feed.PopularTVShowsResponse

class PopularTVShowsFeedViewState(
    val status: Status,
    val error: Throwable?,
    val data: PopularTVShowsResponse?
) {
    fun getPopularTvShows() = data?.results ?: mutableListOf()

    fun isLoading() = status == Status.LOADING

    fun getErrorMessage() = error?.message

    fun shouldShowErrorMessage() = error != null
}