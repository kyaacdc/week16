package com.smarthouse.service;

import com.smarthouse.repository.*;
import com.smarthouse.pojo.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import java.util.*;

import static com.smarthouse.service.util.enums.EnumProductSorter.*;
import static com.smarthouse.service.util.enums.EnumSearcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/app-config.xml")
public class ShopManagerTest {

    @Resource
    private ShopManager shopManager;
    @Resource
    private ProductCardDao productCardDao;
    @Resource
    private CategoryDao categoryDao;
    @Resource
    private CustomerDao customerDao;
    @Resource
    private OrderMainDao orderMainDao;
    @Resource
    private OrderItemDao orderItemDao;
    @Resource
    private AttributeValueDao attributeValueDao;
    @Resource
    private AttributeNameDao attributeNameDao;
    @Resource
    private VisualizationDao visualizationDao;
    @Resource
    private DbRecreator dbRecreator;

    @Before
    public void before() {
        dbRecreator.dropCreateDbAndTables();
    }

    @Test
    public void mustShowCorrectProductAvailability() throws Exception {
        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        ProductCard productCard = new ProductCard("888", "2name", 2222, 34, 45, 4, "xxx", category);

        //Save to DB
        productCard = productCardDao.save(productCard);
        assertTrue(shopManager.isProductAvailable(productCard.getSku()));
    }

    @Test
    public void shouldMakeCorrectOrder() {
        Category category = categoryDao.save(new Category("desc", "catname", null));
        productCardDao.save(new ProductCard("bell", "bell signal", 1234, 100, 1, 1, "bell desc", category));

        shopManager.createOrder("kya1@bk.ru", "23$Yuriy", "0503337178", "my address", 3, "bell");

        assertThat(productCardDao.findBySku("bell").getAmount(), is(equalTo(100)));

    }

    @Test(expected = NoResultException.class)
    public void shouldThrowExceptionWhenMakeOrder() {
        Category category = categoryDao.save(new Category("desc", "catname", null));
        productCardDao.save(new ProductCard("bell", "bell signal", 1234, 100, 1, 1, "bell desc", category));

        shopManager.createOrder("kya@bk.ru", "Yuriy", "0503337178", "my address", 103, "bell");
    }

    @Test
    public void shouldCheckCorrectionOfCompleteOrder() {

        Category category = categoryDao.save(new Category("desc", "catname", null));
        productCardDao.save(new ProductCard("bell", "bell signal", 1234, 100, 1, 1, "bell desc", category));
        productCardDao.save(new ProductCard("ring", "ring signal", 1234, 50, 1, 1, "bell desc", category));
        productCardDao.save(new ProductCard("lord", "lord signal", 1234, 10, 1, 1, "bell desc", category));
        shopManager.createOrder("kya@bk.ru", "Yuriy", "0503337178", "my address", 3, "bell");
        shopManager.createOrder("kya@bk.ru", "Yuriy", "0503337178", "my address", 3, "ring");
        shopManager.createOrder("kya@bk.ru", "Yuriy", "0503337178", "my address", 3, "lord");

        List<OrderMain> orderMains = orderMainDao.findByCustomer(customerDao.findByEmail("kya@bk.ru"));

        for (OrderMain om : orderMains) {
            assertThat(om.getStatus() == 1, is(true));
            List<OrderItem> orderItems = orderItemDao.findByOrderMain(om);
            for (OrderItem oi : orderItems)
                assertThat(productCardDao.findBySku(oi.getProductCard().getSku()).getAmount(), oneOf(100, 50, 10));
        }

        shopManager.submitOrder("kya@bk.ru");

        orderMains = orderMainDao.findByCustomer(customerDao.findByEmail("kya@bk.ru"));

        for (OrderMain om : orderMains) {
            assertThat(om.getStatus() != 1, is(true));
            assertThat(om.getCustomer().getEmail(), is(equalTo("kya@bk.ru")));
            List<OrderItem> orderItems = orderItemDao.findByOrderMain(om);
            for (OrderItem oi : orderItems)
                assertThat(productCardDao.findBySku(oi.getProductCard().getSku()).getAmount(), oneOf(97, 47, 7));
        }
    }

    @Test
    public void shouldCheckRightValidationOfOrderCreation() {
        Category category = categoryDao.save(new Category("desc", "catname", null));
        productCardDao.save(new ProductCard("bell", "bell signal", 1234, 100, 1, 1, "bell desc", category));

        shopManager.createOrder("kya@bk.ru", "Yuriy", "0503337178", "my address", 3, "bell");

        assertThat(productCardDao.findBySku("bell").getAmount(), is(equalTo(100)));
        assertThat(shopManager.validateOrder("kya@bk.ru"), is(true));
    }

    @Test
    public void shouldGetCorrectListOrderedByCustomer() {
        Customer customer = new Customer("anniya@bk.ru", "Yuriy", false, "7585885");
        customerDao.save(customer);

        OrderMain orderMain1 = new OrderMain("1OrderAddress", 1, customer);
        OrderMain orderMain2 = new OrderMain("2OrderAddress", 1, customer);
        OrderMain orderMain3 = new OrderMain("3OrderAddress", 1, customer);

        //Save to DB
        orderMainDao.save(orderMain1);
        orderMainDao.save(orderMain2);
        orderMainDao.save(orderMain3);

        List<OrderMain> ordersMain = orderMainDao.findByCustomer(customer);

        for (OrderMain o : ordersMain) {
            assertThat(o, is(notNullValue()));
            assertThat(o, is(anything()));
            assertThat(o.getAddress(), oneOf("1OrderAddress", "2OrderAddress", "3OrderAddress"));
            assertThat(o, isA(OrderMain.class));
        }
    }

    @Test
    public void shouldGetRealItemOrdersByOrderMain() {
        Category category = categoryDao.save(new Category("desc", "name", null));
        ProductCard productCard = productCardDao.save(new ProductCard("111", "name", 123, 1, 1, 1, "decs", category));
        Customer customer = customerDao.save(new Customer("anniya@bk.ru", "Yuriy", false, "7585885"));
        OrderMain orderMain = orderMainDao.save(new OrderMain("OrderAddress", 1, customer));
        OrderItem orderItem1 = orderItemDao.save(new OrderItem(5, 555, productCard, orderMain));
        OrderItem orderItem2 = orderItemDao.save(new OrderItem(6, 555, productCard, orderMain));
        OrderItem orderItem3 = orderItemDao.save(new OrderItem(7, 555, productCard, orderMain));

        //Save to DB
        orderItemDao.save(orderItem1);
        orderItemDao.save(orderItem2);
        orderItemDao.save(orderItem3);

        List<OrderItem> orderItems = orderItemDao.findByOrderMain(orderMain);

        for (OrderItem o : orderItems) {
            assertThat(o.getAmount(), oneOf(5, 6, 7));
            assertThat(orderItems, is(notNullValue()));
            assertThat(orderItems, is(anything()));
            assertThat(orderItems.get(0), isA(OrderItem.class));
        }
    }

    @Test
    public void shouldCorrectGetItemOrdersByProdCard() {
        Category category = categoryDao.save(new Category("desc", "name", null));
        ProductCard productCard = productCardDao.save(new ProductCard("111", "name", 123, 1, 1, 1, "decs", category));
        Customer customer = customerDao.save(new Customer("anniya@bk.ru", "Yuriy", false, "7585885"));
        OrderMain orderMain = orderMainDao.save(new OrderMain("OrderAddress", 1, customer));
        OrderItem orderItem1 = orderItemDao.save(new OrderItem(5, 555, productCard, orderMain));
        OrderItem orderItem2 = orderItemDao.save(new OrderItem(6, 555, productCard, orderMain));
        OrderItem orderItem3 = orderItemDao.save(new OrderItem(7, 555, productCard, orderMain));

        //Save to DB
        orderItemDao.save(orderItem1);
        orderItemDao.save(orderItem2);
        orderItemDao.save(orderItem3);

        List<OrderItem> orderItems = orderItemDao.findByProductCard(productCard);

        for (OrderItem o : orderItems) {
            assertThat(o.getAmount(), oneOf(5, 6, 7));
            assertThat(orderItems, is(notNullValue()));
            assertThat(orderItems, is(anything()));
            assertThat(orderItems.get(0), isA(OrderItem.class));
        }
    }

    @Test
    public void shouldCheckCorrectionOfSearchAllProductsByDifferCriteria() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        ProductCard productCard1 = new ProductCard("888", "name111", 2222, 34, 45, 4, "xxx", category);
        ProductCard productCard2 = new ProductCard("999", "name111", 2222, 34, 45, 4, "xxx", category);

        //Save to DB
        productCardDao.save(productCard1);
        productCardDao.save(productCard2);

        Set<ProductCard> allProducts = shopManager.findAllProductsByCriteria("Name111");
        assertThat(allProducts.size(), is(equalTo(2)));

        allProducts = shopManager.findAllProductsByCriteria("Desc");
        assertThat(allProducts.size(), is(equalTo(2)));

        allProducts = shopManager.findAllProductsByCriteria("nAme");
        assertThat(allProducts.size(), is(equalTo(2)));
    }

    @Test
    public void shouldCheckCorrectionOfSearchWithFindByName() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        ProductCard productCard1 = new ProductCard("888", "name111", 2222, 34, 45, 4, "xxx", category);
        ProductCard productCard2 = new ProductCard("999", "name111", 2222, 34, 45, 4, "xxx", category);

        //Save in DB
        productCardDao.save(productCard1);
        productCardDao.save(productCard2);

        Set<ProductCard> allProducts = shopManager.findProductsInColumn("naMe111", FIND_BY_NAME);
        assertThat(allProducts.size(), is(equalTo(2)));
    }

    @Test
    public void shouldCheckCorrectionOfSearchByProductDescription() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        ProductCard productCard1 = new ProductCard("888", "name111", 2222, 34, 45, 4, "xxx", category);
        ProductCard productCard2 = new ProductCard("999", "name111", 2222, 34, 45, 4, "xxx", category);

        //Save in DB
        productCardDao.save(productCard1);
        productCardDao.save(productCard2);

        Set<ProductCard> allProducts = shopManager.findProductsInColumn("xXx", FIND_IN_PROD_DESC);
        assertThat(allProducts.size(), is(equalTo(2)));
    }

    @Test
    public void shouldCheckCorrectionOfSearchByCategoryDescription() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        ProductCard productCard1 = new ProductCard("888", "name111", 2222, 34, 45, 4, "xxx", category);
        ProductCard productCard2 = new ProductCard("999", "name111", 2222, 34, 45, 4, "xxx", category);

        //Save in DB
        productCardDao.save(productCard1);
        productCardDao.save(productCard2);

        Set<ProductCard> allProducts = shopManager.findProductsInColumn("DESC", FIND_IN_CATEGORY_DESC);
        assertThat(allProducts.size(), is(equalTo(2)));
    }

    @Test
    public void shouldCheckCorrectionOfSearchAllProductsByCriteriaAndSituatedPlace() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        ProductCard productCard1 = new ProductCard("888", "name111", 2222, 34, 45, 4, "xxx", category);
        ProductCard productCard2 = new ProductCard("999", "name111", 2222, 34, 45, 4, "xxx", category);

        //Save in DB
        productCardDao.save(productCard1);
        productCardDao.save(productCard2);

        Set<ProductCard> allProducts = shopManager.findProductsInColumn("naMe111", FIND_BY_NAME);
        assertThat(allProducts.size(), is(equalTo(2)));
        allProducts = shopManager.findProductsInColumn("xXx", FIND_IN_PROD_DESC);
        assertThat(allProducts.size(), is(equalTo(2)));
        allProducts = shopManager.findProductsInColumn("DESC", FIND_IN_CATEGORY_DESC);
        assertThat(allProducts.size(), is(equalTo(2)));
        allProducts = shopManager.findProductsInColumn("naMe", FIND_IN_CATEGORY_NAME);
        assertThat(allProducts.size(), is(equalTo(2)));
    }

    @Test
    public void shouldGetCorrectRootCategory() throws Exception {

        Category category = new Category("desc", "name", null);

        categoryDao.save(category);

        List<Category> list = shopManager.getRootCategory();
        assertThat(list.get(0).getName(), is(equalTo("name")));
    }

    @Test
    public void shouldGetExistedSubCategories() throws Exception {

        Category category1 = new Category("desc", "name", null);
        category1 = categoryDao.save(category1);
        Category category2 = new Category("desc", "name", category1);
        categoryDao.save(category2);

        List<Category> list = shopManager.getSubCategories(categoryDao.findById(category1.getId()));
        assertThat(list.get(0).getName(), oneOf("catname1", "catname31", "name"));
        assertThat(list.get(0).getId(), isA(Integer.class));
    }

    @Test
    public void shouldGetCorrectProductCardsByCategory() throws Exception {
        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        ProductCard productCard1 = new ProductCard("888", "2name", 2222, 34, 45, 4, "xxx", category);
        ProductCard productCard2 = new ProductCard("999", "3name", 3333, 34, 45, 4, "xxx", category);
        ProductCard productCard3 = new ProductCard("000", "4name", 444, 34, 45, 4, "xxx", category);

        //Save in DB
        productCard1 = productCardDao.save(productCard1);
        productCard2 = productCardDao.save(productCard2);
        productCard3 = productCardDao.save(productCard3);

        List<ProductCard> productCards = shopManager.getProductCardsByCategory(category);

        assertThat(productCard1.getName(), is (equalTo("2name")));
        assertThat(productCard2.getName(), is (equalTo("3name")));
        assertThat(productCard3.getName(), is (equalTo("4name")));
        assertThat(productCards, is(notNullValue()));
        assertThat(productCards, is(anything()));
        assertThat(productCards.get(1), isA(ProductCard.class));
    }

    @Test
    public void shouldCheckOfGetVisualListByProduct() throws Exception {
        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);
        ProductCard productCard = new ProductCard("222", "2name", 2222, 34, 45, 4, "xxx", category);
        productCard = productCardDao.save(productCard);

        //Save in DB
        Visualization visualization1 = new Visualization(2, "1url", productCard);
        Visualization visualization2 = new Visualization(4, "2url", productCard);
        Visualization visualization3 = new Visualization(6, "3url", productCard);

        visualization1 = visualizationDao.save(visualization1);
        visualization2 = visualizationDao.save(visualization2);
        visualization3 = visualizationDao.save(visualization3);

        List<Visualization> visualizations = shopManager.getVisualListByProduct(productCard);

        assertThat(visualizations, is(notNullValue()));
        assertThat(visualizations, is(anything()));
        assertThat(visualizations.get(0), isA(Visualization.class));
        assertThat(visualization1.getUrl(), is(equalTo("1url")));
        assertThat(visualization2.getType(), is(equalTo(4)));
        assertThat(visualization3.getUrl(), is(equalTo("3url")));
    }

    @Test
    public void mustCorrectGetAttrValuesByProduct() throws Exception {
        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);
        ProductCard productCard = new ProductCard("222", "2name", 2222, 34, 45, 4, "xxx", category);
        productCard = productCardDao.save(productCard);
        AttributeName attributeName = new AttributeName("color");
        attributeNameDao.save(attributeName);
        AttributeValue attributeValue = new AttributeValue("1", attributeName, productCard);
        attributeValueDao.save(attributeValue);
        attributeValue = new AttributeValue("2", attributeName, productCard);
        attributeValueDao.save(attributeValue);
        attributeValue = new AttributeValue("3", attributeName, productCard);
        attributeValueDao.save(attributeValue);

        List<AttributeValue> list = shopManager.getAttrValuesByProduct(productCardDao.findBySku("222"));

        list.forEach(a -> {
            assertThat(a.getValue(), oneOf("1", "2", "3"));
            assertThat(a, is(notNullValue()));
            assertThat(a.getValue(), isA(String.class));
            assertThat(a, isA(AttributeValue.class));
        });
    }

    @Test
    public void shouldGetRealAttrValuesByName() throws Exception {
        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);
        ProductCard productCard = new ProductCard("222", "2name", 2222, 34, 45, 4, "xxx", category);
        productCard = productCardDao.save(productCard);
        AttributeName attributeName = new AttributeName("color");
        attributeNameDao.save(attributeName);
        AttributeValue attributeValue = new AttributeValue("1", attributeName, productCard);
        attributeValueDao.save(attributeValue);
        attributeValue = new AttributeValue("2", attributeName, productCard);
        attributeValueDao.save(attributeValue);
        attributeValue = new AttributeValue("3", attributeName, productCard);
        attributeValueDao.save(attributeValue);

        List<AttributeValue> list = shopManager.getAttrValuesByName(attributeNameDao.save(attributeName));

        list.forEach(a -> {
            assertThat(a.getValue(), oneOf("1", "2", "3"));
            assertThat(a, is(notNullValue()));
            assertThat(a.getValue(), isA(String.class));
            assertThat(a, isA(AttributeValue.class));
        });
    }

    @Test
    public void shouldRightSortProductsByPopularity() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 2222, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 2222, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 2222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 4444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(null, SORT_BY_POPULARITY);
        assertThat(productCards.get(3).getLikes(), is(equalTo(4)));
        for(ProductCard p: productCards)
            assertThat(p.getLikes(), oneOf(1,2,3,4));
    }

    @Test
    public void shouldRightSortProductsByPopularityFromCategory() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 2222, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 2222, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 2222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 4444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(category, SORT_BY_POPULARITY);
        assertThat(productCards.get(2).getLikes(), is(equalTo(3)));
        for(ProductCard p: productCards)
            assertThat(p.getLikes(), oneOf(1,2,3));
    }

    @Test
    public void shouldRightSortProductsByUnpopularity() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 2222, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 2222, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 2222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 4444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(null, SORT_BY_UNPOPULARITY);
        assertThat(productCards.get(1).getDislikes(), is(equalTo(2)));
        for(ProductCard p: productCards)
            assertThat(p.getDislikes(), oneOf(1,2,3,4));
    }

    @Test
    public void shouldRightSortProductsByUnpopularityFromCategory() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 2222, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 2222, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 2222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 4444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(category, SORT_BY_UNPOPULARITY);
        assertThat(productCards.get(2).getDislikes(), is(equalTo(3)));
        for(ProductCard p: productCards)
            assertThat(p.getDislikes(), oneOf(1,2,3));
    }

    @Test
    public void shouldCorrectSortAllProductsByName() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 2222, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 2222, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 2222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 4444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(null, SORT_BY_NAME);
        assertThat(productCards.get(1).getName(), is(equalTo("2name")));
    }

    @Test
    public void shouldCorrectSortProductsByNameWithCategory() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("333", "3name", 2222, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(category, SORT_BY_NAME);
        assertEquals(productCards.get(0).getName(), "3name");
    }

    @Test
    public void shouldCorrectSortProductsByNameReversedWithCategory() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 2222, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 2222, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 2222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 4444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);


        List<ProductCard> productCards = shopManager.sortProductCard(category, SORT_BY_NAME_REVERSED);
        assertThat(productCards.get(1).getName(), is(equalTo("2name")));
    }

    @Test
    public void shouldCorrectSortAllProductsByNameReversed() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 2222, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 2222, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 2222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 4444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);


        List<ProductCard> productCards = shopManager.sortProductCard(null, SORT_BY_NAME_REVERSED);
        assertThat(productCards.get(1).getName(), is(equalTo("3name")));
    }

    @Test
    public void mustCorrectAllSortProductsByPrice() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 11111, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 33333, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 22222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 44444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(null, SORT_BY_HIGH_PRICE);
        assertThat(productCards.get(0).getPrice(), is(equalTo(44444)));
    }

    @Test
    public void mustCorrectSortAllProductsByLowPrice() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 11111, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 33333, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 22222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 44444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards =  shopManager.sortProductCard(null, SORT_BY_LOW_PRICE);
        assertThat(productCards.get(0).getPrice(), is(equalTo(11111)));
    }

    @Test
    public void mustCorrectSortProductsByHighPriceWithCategory() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 11111, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 33333, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 22222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 44444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(category, SORT_BY_HIGH_PRICE);
        assertThat(productCards.get(1).getPrice(), is(equalTo(22222)));
    }

    @Test
    public void mustCorrectSortProductsByLowPriceWithCategory() throws Exception {

        Category category = new Category("desc", "name", null);
        category = categoryDao.save(category);

        Category subCategory = new Category("desc", "name", category);
        subCategory = categoryDao.save(subCategory);

        ProductCard productCard = new ProductCard("111", "1name", 11111, 34, 1, 1, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("333", "3name", 33333, 34, 3, 3, "xxx", category);
        productCardDao.save(productCard);
        productCard = new ProductCard("222", "2name", 22222, 34, 2, 2, "xxx", category);
        productCardDao.save(productCard);

        productCard = new ProductCard("444", "4name", 44444, 34, 4, 4, "xxx", subCategory);
        productCardDao.save(productCard);

        List<ProductCard> productCards = shopManager.sortProductCard(category, SORT_BY_LOW_PRICE);
        assertThat(productCards.get(1).getPrice(), is(equalTo(22222)));
    }
}
