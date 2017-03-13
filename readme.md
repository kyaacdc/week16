AttributeName class - saving names of product attributes. And have only one String field named "name"
AttributeValue class saving all values of product attributes. And have two fields: String value and int id - primary key
Category class needs for select some products by different categories. And also have three fields: int id that primary key, String name category and String description of product
Customer class that keeps main information about customer like: email, name, subscribe and phone number. 
String email is an primary key field that identify each clients of internet shop. 
String name (optional) is a field  information about user name (firstname)
Boolean subscribe (no null) is shown that customer want to receive news and good offers from this internet shop.
String phone (optional) save client phone number for make our communications with him.
IderOrder is transitional class that modelling in database amount and total price ordered products before our receive this order. And have two fields: int amount and int total price.
Order class is saving order address and order status. And have String field with String address of customer and have int field for show status of order (sent, not sent, in process)
Product class is a item of our shop. This class show all info about shops products. It have String SKU field for good identify product in warehouse. String name is show name of shops product. Int price saving price of item. Int "amount" show how many this items in the warehouse. Int "like" show how users like this product after by. And String Description also have information about main properties of this product. 
Visualization class respond where saved images and other visualisation components. And have two fields: int type of fields and String url where is situated this visual file.
