package recipebook.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import recipebook.domain.Ingredient;

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
    Ingredient ingredient = ingDao.getByName("Salmon");
    System.out.println(ingredient);
    assertThat(ingredient.getName(), is(equalTo("Salmon")));
  }

}