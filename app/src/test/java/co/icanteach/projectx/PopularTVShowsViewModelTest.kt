package co.icanteach.projectx

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import co.icanteach.RxImmediateSchedulerRule
import co.icanteach.projectx.common.Resource
import co.icanteach.projectx.common.Status
import co.icanteach.projectx.common.ui.applyLoading
import co.icanteach.projectx.data.feed.MoviesRepository
import co.icanteach.projectx.data.feed.response.PopularTVShowsResponse
import co.icanteach.projectx.data.feed.response.PopularTVShowItemResponse
import co.icanteach.projectx.domain.FetchPopularTvShowUseCase
import co.icanteach.projectx.ui.populartvshows.PopularTVShowsFeedViewState
import co.icanteach.projectx.ui.populartvshows.PopularTVShowsViewModel
import co.icanteach.projectx.ui.populartvshows.model.PopularTvShowItem
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

class PopularTVShowsViewModelTest {

    @MockK
    lateinit var fetchPopularTvShowUseCase: FetchPopularTvShowUseCase

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private lateinit var popularTVShowsViewModel: PopularTVShowsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        popularTVShowsViewModel = PopularTVShowsViewModel(fetchPopularTvShowUseCase)
    }

    @Test
    fun `should show loading state first when loading popular tv shows`() {

        // Given
        val mockedObserver = createPopularTVShowsFeedObserver()
        popularTVShowsViewModel.getPopularTvShowsLiveData()
            .observeForever(mockedObserver)

        every { fetchPopularTvShowUseCase.fetchMovies(any()) } returns
                Observable.just(Resource.success(createPopularTVShows()))
                    .compose(applyLoading())

        // When
        popularTVShowsViewModel.fetchMovies(1)

        // Then
        argumentCaptor<PopularTVShowsFeedViewState> {
            verify(mockedObserver, atLeastOnce()).onChanged(capture())
            Truth.assertThat(firstValue.status).isEqualTo(Status.LOADING)
        }
    }

    @Test
    fun `should show success state when loading popular tv shows successfully`() {
        // Given
        val mockedObserver = createPopularTVShowsFeedObserver()
        popularTVShowsViewModel.getPopularTvShowsLiveData()
            .observeForever(mockedObserver)

        every { fetchPopularTvShowUseCase.fetchMovies(any()) } returns
                Observable.just(Resource.success(createPopularTVShows()))
                    .compose(applyLoading())

        // When
        popularTVShowsViewModel.fetchMovies(1)

        // Then
        argumentCaptor<PopularTVShowsFeedViewState> {
            verify(mockedObserver, atLeastOnce()).onChanged(capture())
            Truth.assertThat(secondValue.status).isEqualTo(Status.SUCCESS)
        }
    }

    @Test
    fun `should show error state when there is an error while loading popular tv shows`() {
        // Given
        val mockedObserver = createPopularTVShowsFeedObserver()
        popularTVShowsViewModel.getPopularTvShowsLiveData()
            .observeForever(mockedObserver)

        every { fetchPopularTvShowUseCase.fetchMovies(any()) } returns
                Observable.just(Resource.error<List<PopularTvShowItem>>(Exception("unhandled exception")))
                    .compose(applyLoading())

        // When
        popularTVShowsViewModel.fetchMovies(1)

        // Then
        argumentCaptor<PopularTVShowsFeedViewState> {
            verify(mockedObserver, atLeastOnce()).onChanged(capture())
            Truth.assertThat(secondValue.status).isEqualTo(Status.ERROR)
        }
    }

    private fun createPopularTVShowsFeedObserver(): Observer<PopularTVShowsFeedViewState> = mock { }

    private fun createDummyTvShow(): PopularTvShowItem {
        return PopularTvShowItem(
            name = "Chernobyl",
            imageUrl = "/hlLXt2tOPT6RRnjiUmoxyG1LTFi.jpg",
            rating = "8.3"
        )
    }

    private fun createPopularTVShows(): List<PopularTvShowItem> {
        val popularTvShows = mutableListOf<PopularTvShowItem>()
        for (x in 0..10) {
            popularTvShows.add(createDummyTvShow())
        }
        return popularTvShows
    }
}