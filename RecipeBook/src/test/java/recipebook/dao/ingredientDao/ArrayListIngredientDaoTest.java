package recipebook.dao.ingredientDao;

import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.ingredientdao.ArrayListIngredientDao;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import recipebook.domain.ingredient.Ingredient;

public class ArrayListIngredientDaoTest {

  private IngredientDao ingDao;

  @Before
  public void setUp() {
    ingDao = new ArrayListIngredientDao();
  }

  @Test
  public void getByNameReturnsRightIngredient() {
    ingDao.create(new Ingredient("Salmon"));
    ingDao.create(new Ingredient("Milk"));
    Ingredient ingredient = ingDao.getByNameAndUnit("Salmon", "g");
    assertThat(ingredient.getName(), is(equalTo("Salmon")));
  }

  @Test
  public void getByIdReturnsRightIngredient() {
    Ingredient salmon = ingDao.create(new Ingredient("Salmon"));
    ingDao.create(new Ingredient("Milk"));
    assertThat(salmon.getId(), is(1));
    Ingredient foundIngredient = ingDao.getById(1);
    assertThat(foundIngredient.getName(), is(equalTo("Salmon")));
  }

  @Test
  public void getByIdReturnsNullIfIngredientNotFound() {
    Ingredient salmon = ingDao.create(new Ingredient("Salmon"));
    assertThat(salmon.getId(), is(1));
    Ingredient foundIngredient = ingDao.getById(3);
    assertThat(foundIngredient, is(nullValue()));
  }

}