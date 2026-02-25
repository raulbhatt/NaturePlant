import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.rahul.natureplant.model.Category
import com.rahul.natureplant.viewmodel.CategoryViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CategoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CategoryViewModel

    @Mock
    private lateinit var observer: Observer<List<Category>>

    @Before
    fun setUp() {
        viewModel = CategoryViewModel()
    }

    @Test
    fun `test if categories are loaded on init`() {
        // Given
        val expectedCategories = listOf(
            Category(
                "Succulent",
                "https://images.unsplash.com/photo-1509423350716-97f9360b4e09?w=1000&q=80"
            ),
            Category(
                "Indoor",
                "https://images.unsplash.com/photo-1517191434949-5e90cd67d2b6?w=1000&q=80"
            ),
            Category(
                "Decorative",
                "https://images.unsplash.com/photo-1516048015710-7a3b4c86be43?w=1000&q=80"
            ),
            Category(
                "Hanging",
                "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=1000&q=80"
            ),
            Category(
                "Office",
                "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1000&q=80"
            ),
            Category(
                "Outdoor",
                "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80"
            ),
            Category(
                "Succulent",
                "https://images.unsplash.com/photo-1509423350716-97f9360b4e09?w=1000&q=80"
            ),
            Category(
                "Indoor",
                "https://images.unsplash.com/photo-1517191434949-5e90cd67d2b6?w=1000&q=80"
            ),
            Category(
                "Decorative",
                "https://images.unsplash.com/photo-1516048015710-7a3b4c86be43?w=1000&q=80"
            ),
            Category(
                "Hanging",
                "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=1000&q=80"
            ),
            Category(
                "Office",
                "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1000&q=80"
            ),
            Category(
                "Outdoor",
                "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80"
            )
        )

        // When
        viewModel.categories.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedCategories)
        assertEquals(expectedCategories, viewModel.categories.value)
    }
}