package com.smarthouse.service;

import com.smarthouse.repository.*;
import com.smarthouse.pojo.*;
import com.smarthouse.service.util.validators.EmailValidator;
import com.smarthouse.service.util.enums.EnumProductSorter;
import com.smarthouse.service.util.enums.EnumSearcher;
import org.springframework.data.domain.Sort;

import javax.persistence.NoResultException;
import javax.validation.ValidationException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ShopManager {

    private ProductCardDao productCardDao;
    private CategoryDao categoryDao;
    private CustomerDao customerDao;
    private OrderMainDao orderMainDao;
    private OrderItemDao orderItemDao;
    private VisualizationDao visualizationDao;
    private AttributeValueDao attributeValueDao;

    public ShopManager() {
    }

    public ShopManager(ProductCardDao productCardDao, CategoryDao categoryDao,
                       CustomerDao customerDao, OrderMainDao orderMainDao,
                       OrderItemDao orderItemDao, VisualizationDao visualizationDao,
                       AttributeValueDao attributeValueDao) {
        this.productCardDao = productCardDao;
        this.categoryDao = categoryDao;
        this.customerDao = customerDao;
        this.orderMainDao = orderMainDao;
        this.orderItemDao = orderItemDao;
        this.visualizationDao = visualizationDao;
        this.attributeValueDao = attributeValueDao;
    }

    /**
     * Method createOrder is add or update new Customer into database
     * and also add order info in DB with compute total price
     *
     * @param email   user email address for identy each user by primary key
     * @param name    name of user (optional)
     * @param phone   phone number of user (optional)
     * @param address address for receive order
     * @param amount  amount of products in order
     * @param sku     unique id of each product
     * @throws NoResultException   if amount of products in our order
     *                             less than on warehouse
     * @throws ValidationException if email is not valid
     */
    public void createOrder(String email, String name, String phone,
                            String address, int amount, String sku) {

        EmailValidator emailValidator = new EmailValidator();

        if (!emailValidator.validate(email))
            throw new ValidationException("Email not valid");

        if (!isRequiredAmountOfProductCardAvailable(sku, amount))
            throw new NoResultException();

        ProductCard productCard = productCardDao.findBySku(sku);
        int totalPrice = productCard.getPrice() * amount;
        Customer customer = new Customer(email, name, true, phone);
        customer = customerDao.save(customer);
        OrderMain orderMain = new OrderMain(address, 1, customer);
        orderMain = orderMainDao.save(orderMain);
        orderItemDao.save(new OrderItem(amount, totalPrice, productCard, orderMain));
    }


    //Return product availabitity in storehouse by amount
    public boolean isRequiredAmountOfProductCardAvailable(String sku, int amount) {

        ProductCard productCard = productCardDao.findBySku(sku);
        int productCardAmount = productCard.getAmount();

        return amount <= productCardAmount;
    }

    //Return product availabitity in storehouse
    public boolean isProductAvailable(String sku) {
        return productCardDao.exists(sku);
    }

    /**
     * Method submitOrder need for update amount of ProductCard
     * on warehouse and update status of order in OrderMain in tables
     *
     * @param email is  a user email for making changes
     * @return void type
     * @throws NoResultException if amount of products in our order
     *                           less than on warehouse
     */
    public void submitOrder(String email) {

        if (validateOrder(email) && customerDao.exists(email)) {
            Customer customer = customerDao.findByEmail(email);
            List<OrderMain> ordersByCustomer = getOrdersByCustomer(customer);

            for (OrderMain om : ordersByCustomer) {

                if (om.getStatus() != 1)
                    continue;

                List<OrderItem> orderItemsByOrderMain = getItemOrdersByOrderMain(om);
                for (OrderItem oi : orderItemsByOrderMain) {
                    ProductCard productCard = productCardDao.findBySku(oi.getProductCard().getSku());
                    int newAmount = productCard.getAmount() - oi.getAmount();
                    productCard.setAmount(newAmount);
                    productCardDao.save(productCard);
                }

                om.setStatus(2);
                orderMainDao.save(om);
            }
        } else
            throw new NoResultException("This amount of products not exist on our warehouse");
    }

    /**
     * Method validateOrder need for check amount of ProductCard
     * on warehouse.
     *
     * @param email is  a user email for making changes
     * @return boolean type. True if amount in order >= amount on
     * warehouse
     */
    public boolean validateOrder(String email) {
        boolean isExist = true;
        Customer customer = customerDao.findByEmail(email);
        List<OrderMain> ordersByCustomer = getOrdersByCustomer(customer);
        l1:
        for (OrderMain om : ordersByCustomer) {
            List<OrderItem> itemOrdersByOrderMain = getItemOrdersByOrderMain(om);
            for (OrderItem oi : itemOrdersByOrderMain) {
                if (!isProductAvailable(oi.getProductCard().getSku())) {
                    isExist = false;
                    break l1;
                }
            }
        }
        return isExist;
    }


    public List<OrderMain> getOrdersByCustomer(Customer customer) {
        return orderMainDao.findByCustomer(customer);
    }

    public List<OrderItem> getItemOrdersByOrderMain(OrderMain orderMain) {
        return orderItemDao.findByOrderMain(orderMain);
    }

    public List<OrderItem> getItemOrdersByProdCard(ProductCard productCard) {
        return orderItemDao.findByProductCard(productCard);
    }

    /**
     * Method findProductsByCriteriaInAllPlaces need for find any ProductCard
     * on warehouse by String criteria in all Db     *
     *
     * @param criteria String is  a string for the find
     * @return Set<ProductCard> type with found set of products
     */
    public Set<ProductCard> findAllProductsByCriteria(String criteria) {
        Set<ProductCard> result = new LinkedHashSet<>();

        ProductCard productCard = productCardDao.findBySku(criteria);
        if (productCard != null)
            result.add(productCard);

        List<ProductCard> list = productCardDao.findByNameIgnoreCase(criteria);
        if (list.size() > 0)
            result.addAll(list);

        list = productCardDao.findByProductDescriptionIgnoreCase(criteria);
        if (list.size() > 0)
            result.addAll(list);

        result.addAll(getProductsByCategoryDescription(criteria));
        result.addAll(getProductsByCategoryName(criteria));

        return result;
    }

    /**
     * Method findProductsInColumn need for find any ProductCard
     * on warehouse by String criteria, in custom place.
     *
     * @param criteria     String is  a string for the find
     * @param placeForFind enumeration for choose sort criteria:
     *                     FIND_ALL,
     *                     FIND_BY_NAME,
     *                     FIND_IN_PROD_DESC,
     *                     FIND_IN_CATEGORY_NAME,
     *                     FIND_IN_CATEGORY_DESC;
     * @return Set<ProductCard> found results of products
     */
    public Set<ProductCard> findProductsInColumn(String criteria, EnumSearcher placeForFind) {

        Set<ProductCard> result = new LinkedHashSet<>();
        List<Category> categoryList;

        switch (placeForFind) {
            case FIND_BY_NAME:
                result.addAll(productCardDao.findByNameIgnoreCase(criteria));
                return result;
            case FIND_IN_PROD_DESC:
                result.addAll(productCardDao.findByProductDescriptionIgnoreCase(criteria));
                return result;
            case FIND_IN_CATEGORY_NAME:
                return getProductsByCategoryName(criteria);
            case FIND_IN_CATEGORY_DESC:
                return getProductsByCategoryDescription(criteria);
            default:
                throw new NoResultException();
        }
    }

// Methods for getting lists of various items

    public List<Category> getRootCategory() {
        return categoryDao.findByCategory(null);
    }


    public List<Category> getSubCategories(Category category) {
        return categoryDao.findByCategory(category);
    }

    public List<ProductCard> getProductCardsByCategory(Category category) {
        return productCardDao.findByCategory(category);
    }

    public List<Visualization> getVisualListByProduct(ProductCard productCard) {
        return visualizationDao.findByProductCard(productCard);
    }

    public List<AttributeValue> getAttrValuesByProduct(ProductCard productCard) {
        return attributeValueDao.findByProductCard(productCard);
    }

    public List<AttributeValue> getAttrValuesByName(AttributeName attributeName) {
        return attributeValueDao.findByAttributeName(attributeName);
    }

    public List<ProductCard> sortProductCard(Category category, EnumProductSorter criteria) {

        Sort sort;

        switch (criteria) {
            case SORT_BY_NAME:
                sort = new Sort(new Sort.Order(Sort.Direction.ASC, "name")); break;
            case SORT_BY_NAME_REVERSED:
                sort = new Sort(new Sort.Order(Sort.Direction.DESC, "name")); break;
            case SORT_BY_LOW_PRICE:
                sort = new Sort(new Sort.Order(Sort.Direction.ASC, "price")); break;
            case SORT_BY_HIGH_PRICE:
                sort = new Sort(new Sort.Order(Sort.Direction.DESC, "price")); break;
            case SORT_BY_POPULARITY:
                sort = new Sort(new Sort.Order(Sort.Direction.ASC, "likes")); break;
            case SORT_BY_UNPOPULARITY:
                sort = new Sort(new Sort.Order(Sort.Direction.ASC, "dislikes")); break;
            default:
                throw new NoResultException();
        }

        return category == null ? productCardDao.findAllBy(sort) : productCardDao.findByCategory(category, sort);
    }

    //Private helpful methods

    private Set<ProductCard> getProductsByCategoryDescription(String criteria) {

        Set<ProductCard> result = new LinkedHashSet<>();

        List<Category> categoryList = categoryDao.findByDescriptionIgnoreCase(criteria);
        if (categoryList.size() > 0) {
            for (Category c : categoryList) {
                List<ProductCard> list = productCardDao.findByCategory(c);
                result.addAll(list);
            }
        }

        return result;
    }

    private Set<ProductCard> getProductsByCategoryName(String criteria) {

        Set<ProductCard> result = new LinkedHashSet<>();

        List<Category> categoryList = categoryDao.findByNameIgnoreCase(criteria);
        if (categoryList.size() > 0) {
            for (Category c : categoryList) {
                List<ProductCard> list = productCardDao.findByCategory(c);
                result.addAll(list);
            }
        }

        return result;
    }
}
