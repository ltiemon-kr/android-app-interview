package com.kroger.android.interview.hackernews.repository

import com.kroger.android.interview.hackernews.network.HackerNewsService
import com.kroger.android.interview.hackernews.network.Item
import com.kroger.android.interview.hackernews.network.local.LocalHackerNewsService
import com.kroger.android.interview.hackernews.network.local.KrogerFileLoader
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.SingleSubject
import java.io.BufferedReader
import java.io.FileInputStream
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

private const val DUMMY_ID = 123456
private const val DUMMY_TITLE = "Breaking News! We live in a simulation!"
private const val DUMMY_AUTHOR = "310n msk"
private const val DUMMY_TYPE = "story"
private const val DUMMY_TIME = 12321443

const val ASSET_BASE_PATH = "../app/src/main/assets/"
class TestFileLoader : KrogerFileLoader {
    override fun readFile(fileName: String): String {
        return FileInputStream(ASSET_BASE_PATH + fileName)
            .bufferedReader().use(BufferedReader::readText)
    }
}

@RunWith(JUnit4::class)
class HackerNewsRepositoryTest {
    @JvmField
    val rxSchedulerRule = TestScheduler()

    private val topStoriesSubject: SingleSubject<List<Int>> = SingleSubject.create()
    private val itemSubject: SingleSubject<Item> = SingleSubject.create()

    private val hackerNewsService: HackerNewsService = mock {
        on { getTopStoriesRx() } doReturn topStoriesSubject
        on { getItemRx(any()) } doReturn itemSubject
    }

    // HackerNewsRepository that uses the mocked version of HackerNewsService
    private val underTestMock = HackerNewsRepository(hackerNewsService)

    // HackerNewsRepository that uses the LocalHackerNewsService, which fetches from disk
    private val testFileReader = TestFileLoader()
    private val localHackerNewsService = LocalHackerNewsService(testFileReader)
    private val underTestFake = HackerNewsRepository(localHackerNewsService)

    private val dummyItem = Item(
        id = DUMMY_ID,
        title = DUMMY_TITLE,
        null,
        DUMMY_TIME,
        DUMMY_TYPE,
        DUMMY_AUTHOR, null, null, null, null
    )

    @Test
    fun `get top stories`() {
        val observer = underTestMock.getTopStoriesRx().test()

        topStoriesSubject.onSuccess(listOf(1, 2, 3, 4))

        rxSchedulerRule.triggerActions()

        observer.assertValue { it.size == 4 }
    }

    @Test
    fun `get no top stories`() {
        val observer = underTestMock.getTopStoriesRx().test()

        topStoriesSubject.onSuccess(emptyList())

        rxSchedulerRule.triggerActions()

        observer.assertValue { it.isEmpty() }
    }

    @Test
    fun `get item`() {
        val observer = underTestMock.getItemRx(1).test()

        itemSubject.onSuccess(dummyItem)

        rxSchedulerRule.triggerActions()

        // should return item defined in the mock in this test file.
        observer.assertValue {
            it.time == DUMMY_TIME &&
                it.id == DUMMY_ID &&
                it.title == DUMMY_TITLE &&
                it.by == DUMMY_AUTHOR &&
                it.type == DUMMY_TYPE
        }
    }

    @Test
    fun `get item from local disk`() {
        val observer = underTestFake.getItemRx(1).test()

        rxSchedulerRule.triggerActions()

        // should return the item in the items.json file in the assets directory
        observer.assertValue { it.by == "dhouston" && it.type == "story" }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `no item found from local disk`() {
        // make sure that an exception is thrown with an invalid Id.
        underTestFake.getItemRx(100000).test()
    }
}
