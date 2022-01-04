package order;

import user.User;
import user.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class OrderService {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private OrderRepository orderRepository = new OrderRepository();
//    static {
//        FileHandler fileHandler = null;
//        try {
//        fileHandler = new FileHandler(OrderService.class.getName() + ".log");
//        fileHandler.setFormatter(new SimpleFormatter());
//        Filter filterAll = s -> false;
//        LOGGER.setFilter((filterAll));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        LOGGER.addHandler(fileHandler);
//    }

    //get all logic
    public List<Order> getAllOrders() {
        LOGGER.log(Level.INFO, "Getting all the orders");
        return orderRepository.findAll();
    }

    //get by user logic
    public List<Order> getAllOrdersByUser(User user) {
        if (UserRepository.getDummyDataList().contains(user)) {
            LOGGER.log(Level.INFO, String.format("Getting all orders for user id: %d and username: %s", user.getId(), user.getUserName()));
            return orderRepository.findByUser(user);
        } else {
            try {
                throw new Exception("User doesn't exist");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, String.format("Cannot get orders for a non existing user, username: %s id: %d", user.getUserName(), user.getId()));
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    //add order logic
    public boolean addOrder(Order order) {
        if (order.getOrderDateTime().isAfter(LocalDateTime.now())) {
            try {
                throw new Exception("Can't order in the future!");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, String.format("Placing order failed, cannot order in the future. User: %s Order: %s", order.getUser().getId(), order.getId()));
                e.printStackTrace();
                return false;
            }
        }
        if (order.getProducts().size() < 1) {
            try {
                throw new Exception("Order must consist of at least one product!");
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.log(Level.WARNING, String.format("Cannot get empty order, username: %s id: %d", order.getUser().getUserName(), order.getUser().getId()));
                return false;
            }
        }
        LOGGER.log(Level.INFO, String.format("Saving order: %s for user %d", order.getProducts(), order.getUser().getId()));
        return orderRepository.save(order);
    }

    //delete logic
    public boolean deleteOrder(Order order) {
        if (order.getOrderStatus() == OrderStatus.COMPLETED) {
            try {
                throw new Exception("Can't cancel a completed order!");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, String.format("Cannot cancel a completed order, User: %s Order: %s", order.getUser().getId(), order.getId()));
                e.printStackTrace();
            }
        } else if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            try {
                throw new Exception("Order was already cancelled!");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, String.format("Order was already cancelled!, User: %s Order: %s", order.getUser().getId(), order.getId()));
                e.printStackTrace();
            }
        }
        return orderRepository.remove(order);
    }

}


