
public class StockManager {
    private static class StockNode {
        String id;
        float price;

        AVLTree<Long, Float> changes;
    }

    private static class PriceKey implements Comparable<PriceKey> {
        Float price;
        String id;

        public PriceKey(Float price, String id) {
            this.price = price;
            this.id = id;
        }

        @Override
        public int compareTo(PriceKey o) {
            // if prices equal, compare by stock id
            if (this.price.equals(o.price)) {
                return this.id.compareTo(o.id);
            }

            return this.price.compareTo(o.price);
        }
    }

    private AVLTree<String, StockNode> stocks;
    private AVLTree<PriceKey, String> stocksByPrices; // values are ids

    public StockManager() {
    }

    // 1. Initialize the system
    public void initStocks() {
        this.stocks = new AVLTree<>();
        this.stocksByPrices = new AVLTree<>();
    }

    // 2. Add a new stock
    public void addStock(String stockId, long timestamp, Float price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        if (this.stocks.find(stockId) != null) {
            throw new IllegalArgumentException("Stock already exists");
        }

        StockNode stock = new StockNode();
        stock.id = stockId;
        stock.price = price;
        stock.changes = new AVLTree<Long, Float>();
        stock.changes.insert(timestamp, price);

        this.stocks.insert(stockId, stock);
        this.stocksByPrices.insert(new PriceKey(price, stockId), stockId);
    }

    // 3. Remove a stock
    public void removeStock(String stockId) {
        AVLTree.Node<String, StockNode> stockNode = this.stocks.find(stockId);
        if (stockNode == null) {
            throw new IllegalArgumentException("Stock not found");
        }

        this.stocksByPrices.delete(new PriceKey(stockNode.data.price, stockId));
        this.stocks.delete(stockId);
    }

    // 4. Update a stock price
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
        if (priceDifference == 0) {
            throw new IllegalArgumentException("Price difference must be non-zero");
        }

        if (this.stocks.find(stockId) == null) {
            throw new IllegalArgumentException("Stock not found");
        }

        AVLTree.Node<String, StockNode> stockNode = this.stocks.find(stockId);
        if (stockNode == null) {
            throw new IllegalArgumentException("Stock not found");
        }

        StockNode stock = stockNode.data;

        // remove old price
        this.stocksByPrices.delete(new PriceKey(stock.price, stockId));
        // update price
        stock.price += priceDifference;
        stock.changes.insert(timestamp, priceDifference);

        this.stocksByPrices.insert(new PriceKey(stock.price, stockId), stockId);
    }

    // 5. Get the current price of a stock
    public Float getStockPrice(String stockId) {
        AVLTree.Node<String, StockNode> stockNode = this.stocks.find(stockId);
        if (stockNode == null) {
            throw new IllegalArgumentException("Stock not found");
        }

        return stockNode.data.price;
    }

    // 6. Remove a specific timestamp from a stock's history
    public void removeStockTimestamp(String stockId, long timestamp) {
        AVLTree.Node<String, StockNode> stockNode = this.stocks.find(stockId);
        if (stockNode == null) {
            throw new IllegalArgumentException("Stock not found");
        }

        StockNode stock = this.stocks.find(stockId).data;

        if (stock.changes.find(timestamp) == null) {
            throw new IllegalArgumentException("Timestamp not found");
        }

        // remove old price
        this.stocksByPrices.delete(new PriceKey(stock.price, stockId));
        // update price
        stock.price -= stock.changes.find(timestamp).data; // not ideal but ok
        stock.changes.delete(timestamp);
        // insert new price
        this.stocksByPrices.insert(new PriceKey(stock.price, stockId), stockId);
    }

    // 7. Get the amount of stocks in a given price range
    public int getAmountStocksInPriceRange(Float price1, Float price2) {
        return this.stocksByPrices.countInRange(new PriceKey(price1, ""),
                new PriceKey(price2, ""));
    }

    // 8. Get a list of stock IDs within a given price range
    public String[] getStocksInPriceRange(Float price1, Float price2) {
        Object[] stocks = this.stocksByPrices.getNodesInRange(new PriceKey(price1, ""),
                new PriceKey(price2, "~~~~~~~~~~~~"));

        String[] result = new String[stocks.length];
        for (int i = 0; i < stocks.length; i++) {
            result[i] = (String) stocks[i];
        }

        return result;
    }
}
