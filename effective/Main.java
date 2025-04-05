package effective;

public class Main {
    public static void main(String[] args) {
        NutritionFacts nutritionFacts = new NutritionFacts.Builder(240, 8).calories(100).sodium(35).build();
        int servingSize = nutritionFacts.getServingSize();
        int servings = nutritionFacts.getServings();
        int calories = nutritionFacts.getCalories();
        int fat = nutritionFacts.getFat();
        int sodium = nutritionFacts.getSodium();
        int carbohydrate = nutritionFacts.getCarbohydrate();
        System.out.println(servingSize + " " + servings + " " + calories + " " + fat + " " + sodium + " " + carbohydrate);
    }
}
