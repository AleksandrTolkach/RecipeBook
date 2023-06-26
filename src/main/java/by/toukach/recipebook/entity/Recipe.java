package by.toukach.recipebook.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A class that represents Recipe entity.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Recipe extends BaseEntity {
  @Column(name = "recipe_name", length = 75, nullable = false)
  private String recipeName;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", referencedColumnName = "id")
  private User author;
  @Column(nullable = false)
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "recipe_id")
  private List<Step> steps;
  @Column(nullable = false)
  private Integer cookTime;
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "recipe_id")
  private List<Ingredient> ingredients;
  @ManyToMany
  @JoinTable(
      name = "saved_recipe",
      joinColumns = @JoinColumn(name = "recipe_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  @EqualsAndHashCode.Exclude
  private List<User> saves;
  private String recipePhotoName;

  /**
   * This method responsible for returning unmodifiable list of steps.
   *
   * @return unmodifiable list with steps
   */
  public List<Step> getSteps() {
    return steps != null ? Collections.unmodifiableList(steps) : null;
  }

  /**
   * This method responsible for returning unmodifiable list of ingredients.
   *
   * @return unmodifiable list with steps
   */
  public List<Ingredient> getIngredients() {
    return ingredients != null ? Collections.unmodifiableList(ingredients) : null;
  }

  /**
   * The method is responsible for adding steps to recipe.
   *
   * @param newSteps steps to be added
   */
  public void addSteps(List<Step> newSteps) {
    if (steps == null) {
      steps = newSteps;
    } else {
      steps.addAll(newSteps);
    }
  }

  /**
   * This method is responsible for removing all step entities that are represented in the recipe.
   */
  public void removeAllSteps() {
    if (steps != null) {
      steps.clear();
    }
  }

  /**
   * The method is responsible for adding ingredients to recipe.
   *
   * @param newIngredients ingredients to be added
   */
  public void addIngredients(List<Ingredient> newIngredients) {
    if (ingredients == null) {
      ingredients = newIngredients;
    } else {
      ingredients.addAll(newIngredients);
    }
  }

  /**
   * This method is responsible for removing all ingredient entities
   * that are represented in the recipe.
   */
  public void removeAllIngredients() {
    if (ingredients != null) {
      ingredients.clear();
    }
  }

  /**
   * This method is responsible for add/delete recipe from favorites.
   *
   * @param save The user for whom the add/delete is performed
   */
  public void toggleSave(User save) {
    if (!saves.contains(save)) {
      saves.add(save);
    } else {
      saves.remove(save);
    }
  }
}
