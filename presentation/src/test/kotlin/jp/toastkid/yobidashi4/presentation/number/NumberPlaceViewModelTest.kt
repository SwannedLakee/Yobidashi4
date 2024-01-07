package jp.toastkid.yobidashi4.presentation.number

import androidx.compose.runtime.mutableStateOf
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.number.GameRepository
import jp.toastkid.yobidashi4.domain.service.number.GameFileProvider
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class NumberPlaceViewModelTest {

    private lateinit var numberPlaceViewModel: NumberPlaceViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var repository: GameRepository

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { repository } bind(GameRepository::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        every { repository.save(any(), any()) } just Runs
        every { repository.delete(any()) } just Runs

        numberPlaceViewModel = NumberPlaceViewModel()
        numberPlaceViewModel.initialize(20)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun initializeSolving() {
        numberPlaceViewModel.initializeSolving()
    }

    @Test
    fun setGame() {
        numberPlaceViewModel.setGame(mockk(relaxed = true))
    }

    @Test
    fun setCorrect() {
        numberPlaceViewModel.setCorrect()

        assertFalse(numberPlaceViewModel.loading().value)
    }

    @Test
    fun masked() {
        assertNotNull(numberPlaceViewModel.masked())
    }

    @Test
    fun place() {
        numberPlaceViewModel.place(0, 0, 1, {})
    }

    @Test
    fun useHint() {
        numberPlaceViewModel.useHint(0, 0, mutableStateOf(""), {})
    }

    @Test
    fun saveCurrentGame() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()

        numberPlaceViewModel.saveCurrentGame()

        verify { repository.save(any(), any()) }
    }

    @Test
    fun pickSolving() {
        numberPlaceViewModel.pickSolving(0, 0)
    }

    @Test
    fun deleteGame() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()

        numberPlaceViewModel.deleteGame()

        verify { anyConstructed<GameFileProvider>().invoke() }
        verify { repository.delete(any()) }
    }

    @Test
    fun deleteGameProvidedNullCase() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns null

        numberPlaceViewModel.deleteGame()

        verify { anyConstructed<GameFileProvider>().invoke() }
        verify { repository wasNot called }
    }

    @Test
    fun setMaskingCount() {
        every { setting.setMaskingCount(any()) } just Runs

        numberPlaceViewModel.setMaskingCount(30)

        verify { setting.setMaskingCount(30) }
    }

    @Test
    fun renewGame() {
        mockkConstructor(NumberPlaceGame::class, GameFileProvider::class)
        every { anyConstructed<NumberPlaceGame>().initialize(any()) } just Runs
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()
        every { setting.getMaskingCount() } returns 20

        numberPlaceViewModel.renewGame()

        verify { anyConstructed<NumberPlaceGame>().initialize(any()) }
        verify { anyConstructed<GameFileProvider>().invoke() }
    }

    @Test
    fun startNewGame() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()
        every { setting.getMaskingCount() } returns 20

        numberPlaceViewModel.startNewGame()

        verify { anyConstructed<GameFileProvider>().invoke() }
    }

    @Test
    fun showMessageSnackbar() {
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs

        numberPlaceViewModel.showMessageSnackbar(true, {})

        verify { mainViewModel.showSnackbar(any(), any(), any()) }
    }

    @Test
    fun start() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()
        mockkStatic(Files::class)
        every { Files.size(any()) } returns 1L
        val game = mockk<NumberPlaceGame>()
        every { game.masked() } returns mockk()
        every { repository.load(any()) } returns game

        runBlocking {
            numberPlaceViewModel.start()

            verify(inverse = true) { setting.getMaskingCount() }
        }
    }

    @Test
    fun startWhenLoadedGameIsNull() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()
        mockkStatic(Files::class)
        every { Files.size(any()) } returns 1L
        every { repository.load(any()) } returns null
        every { setting.getMaskingCount() } returns 1

        runBlocking {
            numberPlaceViewModel.start()

            verify { setting.getMaskingCount() }
        }
    }

    @Test
    fun onCellLongClick() {
        numberPlaceViewModel = spyk(numberPlaceViewModel)
        every { numberPlaceViewModel.useHint(any(), any(), any(), any()) } just Runs
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs

        numberPlaceViewModel.onCellLongClick(1, 1, mutableStateOf(""))
        slot.captured.invoke()

        verify { mainViewModel.showSnackbar(any(), any(), any()) }
        verify { numberPlaceViewModel.useHint(any(), any(), any(), any()) }
    }

}