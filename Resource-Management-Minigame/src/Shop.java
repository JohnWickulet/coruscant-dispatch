public class Shop {

    public boolean buy(ItemType item, Game game) {
        if (game.getCurrency() < item.price) return false;
        game.useCurrency(item.price);
        game.inventory.add(item);
        return true;
    }
}
