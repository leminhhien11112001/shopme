package com.shopme.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Review;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.customer.CustomerRepository;
import com.shopme.order.OrderDetailRepository;
import com.shopme.brand.BrandRepository;
import com.shopme.category.CategoryRepository;
import com.shopme.product.ProductRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewService {
	public static final int REVIEWS_PER_PAGE = 5;

	@Autowired 
	private ReviewRepository repo;

	@Autowired 
	private OrderDetailRepository orderDetailRepo;
	
	@Autowired 
	private ProductRepository productRepo;
	
	@Autowired 
	private BrandRepository brandRepo;
	
	@Autowired
	private CategoryRepository cateRepo;
	
	@Autowired
	private CustomerRepository cusRepository;
	
	public List<Review> findAll(){
		return repo.findAll();
	}
	
	public List<Review> findByCustomer(Integer idCustomer){
		return repo.findByCustomer(idCustomer);
	}
	
	public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum, 
			String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

		if (keyword != null) {
			return repo.findByCustomer(customer.getId(), keyword, pageable);
		}

		return repo.findByCustomer(customer.getId(), pageable);
	}

	public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException {
		Review review = repo.findByCustomerAndId(customer.getId(), reviewId);
		if (review == null) 
			throw new ReviewNotFoundException("Customer doesn not have any reviews with ID " + reviewId);

		return review;
	}
	
	public Page<Review> list3MostRecentReviewsByProduct(Product product) {
		Sort sort = Sort.by("reviewTime").descending();
		Pageable pageable = PageRequest.of(0, 3, sort);

		return repo.findByProduct(product, pageable);		
	}
	
	public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

		return repo.findByProduct(product, pageable);
	}
	
	public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = repo.countByCustomerAndProduct(customer.getId(), productId);
		return count > 0;
	}
	
	public boolean didCustomerReview(Customer customer) {
		Long count = repo.countByCustomer(customer.getId());
		return count > 0;
	}

	public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = orderDetailRepo.countByProductAndCustomerAndOrderStatus(productId, customer.getId(), "DELIVERED");
		return count > 0;
	}
	
	public Review save(Review review) {
		review.setReviewTime(new Date());
		Review savedReview = repo.save(review);

		Integer productId = savedReview.getProduct().getId();		
		productRepo.updateReviewCountAndAverageRating(productId);

		return savedReview;
	}
	
	
	//contentBasedRecommendation
	public ArrayList<Product> contentBasedRecommendation(Integer customerId, Integer numberOfProduct) {
		ArrayList<Product> recommendProducts = contentBase(customerId, numberOfProduct);
		return recommendProducts;
	}

	private ArrayList<Product> contentBase(int userId, int numberOfProduct) {
		HashMap<Integer, Double> userItemsMap = new HashMap<>();
		ArrayList<Integer> brandList = new ArrayList<>();
		ArrayList<Integer> categoryList = new ArrayList<>();
		ArrayList<Product> knownItemList = new ArrayList<>();
		ArrayList<Product> unknownItemList = new ArrayList<>();

		getDataFromDataBase(userId, brandList, categoryList, userItemsMap, knownItemList, unknownItemList);

		HashMap<String, Double> weightMap;
		weightMap = generateWeightMap(userItemsMap, brandList, categoryList, knownItemList);
		
		sortByPreference(unknownItemList, weightMap);

		ArrayList<Product> result = new ArrayList<>();
		for (int i = 0; i < unknownItemList.size() && i < numberOfProduct; ++i) {
			result.add(unknownItemList.get(i));
		}
		return result;
	}

	private void sortByPreference(ArrayList<Product> unknownItemList, HashMap<String, Double> weightMap) {
		HashMap<Integer, Double> predictedScores = new HashMap<>();

		double brandScore, categoryScore;
		
		for (Product p : unknownItemList) {
			brandScore = weightMap.get("b" + p.getBrand().getId());
			categoryScore = weightMap.get("c" + p.getCategory().getId());
			predictedScores.put(p.getId(), brandScore + categoryScore);
		}      
        
		Collections.sort(unknownItemList, new Comparator<Product>() {
			public int compare(Product item1, Product item2) {
				Double d1 = predictedScores.get(item1.getId());
				Double d2 = predictedScores.get(item2.getId());

		        if (d1 == null && d2 == null) {
		            return 0;
		        } else if (d1 == null) {
		            return 1; 
		        } else if (d2 == null) {
		            return -1; 
		        }

		        return Double.compare(d2, d1);
			}
		});
		
		for(int i=0; i<6 && i<unknownItemList.size(); ++i) {
			System.out.println(unknownItemList.get(i).getId() + ": " + predictedScores.get(unknownItemList.get(i).getId()));
		}
	}

	private HashMap<String, Double> generateWeightMap(HashMap<Integer, Double> userItemsMap,
			ArrayList<Integer> brandList, ArrayList<Integer> categoryList, ArrayList<Product> knownItemList) {
		HashMap<Integer, HashMap<String, Double>> itemScoreMap = new HashMap<>();
		for (Product p : knownItemList) {
			itemScoreMap.put(p.getId(), new HashMap<>());
			for (Integer brandId : brandList) {
				if (p.getBrand().getId() == brandId) {
					itemScoreMap.get(p.getId()).put("b" + brandId, userItemsMap.get(p.getId()));
				} else {
					itemScoreMap.get(p.getId()).put("b" + brandId, 0.0);
				}
			}
			for (Integer categoryId : categoryList) {
				if (p.getCategory().getId() == categoryId) {
					itemScoreMap.get(p.getId()).put("c" + categoryId, userItemsMap.get(p.getId()));
				} else {
					itemScoreMap.get(p.getId()).put("c" + categoryId, 0.0);
				}
			}
		}

		HashMap<String, Double> weightMap = new HashMap<>();
		double sum = 0;
		for (Integer i : itemScoreMap.keySet()) {
			for (String j : itemScoreMap.get(i).keySet()) {
				sum += itemScoreMap.get(i).get(j);
				if (weightMap.containsKey(j)) {
					weightMap.put(j, weightMap.get(j) + itemScoreMap.get(i).get(j));
				} else {
					weightMap.put(j, itemScoreMap.get(i).get(j));
				}
			}
		}

		for (String s : weightMap.keySet()) {
			weightMap.put(s, weightMap.get(s) / sum);
		}

		return weightMap;
	}

	private void getDataFromDataBase(int userId, List<Integer> brandList, List<Integer> categoryList,
			HashMap<Integer, Double> userItemsMap, List<Product> knownItemList,
			List<Product> unknownItemList) {
		List<Brand> listBrands = (List<Brand>) brandRepo.findAll();
		List<Category> listCategories = cateRepo.findAllEnabled();
		
		for(Brand b: listBrands) {
			brandList.add(b.getId());
		}
		
		for(Category c: listCategories) {
			categoryList.add(c.getId());
		}

		List<Review> listReviews = repo.findByCustomer(userId);
		ArrayList<Integer> knownItemIds = new ArrayList<>();
		
		for(Review r: listReviews) {
			userItemsMap.put(r.getProduct().getId(), (double) r.getRating());
			knownItemIds.add(r.getProduct().getId());
		}

		List<Product> itemList = (List<Product>) productRepo.findAll();

		for (Product p : itemList) {
			if (knownItemIds.contains(p.getId())) {
				knownItemList.add(p);
			} else {
				unknownItemList.add(p);
			}
		}
	}
	
	//itemToItemRecommendation
	public List<Product> itemToItemRecommendation(Integer CustomerId, Integer numberOfItems) {
		HashMap<Integer, HashMap<Integer, Double>> map = new HashMap<>();
        ArrayList<Integer> customerList = new ArrayList<>();
        ArrayList<Integer> itemList = new ArrayList<>();
        HashMap<Integer, HashMap<Integer, Double>> simMap = new HashMap<>();

        generateMatrix(map, customerList, itemList, simMap);

        ArrayList<Integer> recommendedItems = recommendItem(map, CustomerId, simMap, numberOfItems);
        
        List<Product> recommendProducts = new ArrayList<>();
        
        for(Integer idProduct: recommendedItems) {
        	recommendProducts.add(productRepo.getById(idProduct));
        }
        
		return recommendProducts;
	}
	
	private ArrayList<Integer> recommendItem(
            HashMap<Integer, HashMap<Integer, Double>> map,
            int customerId,
            HashMap<Integer, HashMap<Integer, Double>> simMap,
            int numberOfItems
    ) {
        ArrayList<Integer> result = fillInMissingValue(map, customerId, simMap);
        ArrayList<Integer> temp = new ArrayList<>();

        for (int i = 0; (i < numberOfItems) && (i < result.size()); ++i) {
            temp.add(result.get(i));
        }
        return temp;
    }

	private ArrayList<Integer> fillInMissingValue(
            HashMap<Integer, HashMap<Integer, Double>> map,
            int customerId,
            HashMap<Integer, HashMap<Integer, Double>> simMap
    ) {
    	
        HashMap<Integer, Double> predicted = new HashMap<>();
        
        ArrayList<Double> score = new ArrayList<>();
        ArrayList<Double> sim = new ArrayList<>();
        ArrayList<Integer> missingItem = new ArrayList<>();
        ArrayList<Integer> filledInItem = new ArrayList<>();
        double temp;
        for (Integer i : map.get(customerId).keySet()) {
            temp = map.get(customerId).get(i);
            if (temp != -1.0) {
                score.add(temp);
                filledInItem.add(i);
            } else {
                missingItem.add(i);
            }
        }

        double predictedResult;
        for (Integer missingItemId : missingItem) {
            for (Integer filledInItemId : filledInItem) {
                sim.add(simMap.get(missingItemId).get(filledInItemId));
            }
            predictedResult = predict(score, sim);
            predicted.put(missingItemId, predictedResult);
            sim.clear();
        }
        
        Collections.sort(missingItem, new Comparator<Integer>() {
            public int compare(Integer item1, Integer item2) {                
                Double d1 = predicted.get(item1);
				Double d2 = predicted.get(item2);

		        if (d1 == null && d2 == null) {
		            return 0;
		        } else if (d1 == null) {
		            return 1; 
		        } else if (d2 == null) {
		            return -1; 
		        }

		        return Double.compare(d2, d1);
            }
        });
        
        for(int i=0; i<6 && i<missingItem.size(); ++i) {
        	System.out.println(missingItem.get(i) + ": " + predicted.get(missingItem.get(i)));
        }
        
        return missingItem;
    }

	private void generateMatrix(
            HashMap<Integer, HashMap<Integer, Double>> map,
            ArrayList<Integer> customerList,
            ArrayList<Integer> itemList,
            HashMap<Integer, HashMap<Integer, Double>> simMap
    ) {
    	List<Customer> listCustomers = (List<Customer>) cusRepository.findAll();
    	List<Product> listProducts = (List<Product>) productRepo.findAll();
    	List<Review> listReviews = repo.findAll();
    	
    	for(Customer c: listCustomers) {
    		customerList.add(c.getId());
    		map.put(c.getId(), new HashMap<>());
    	}
    	
    	for(Product p: listProducts) {
    		itemList.add(p.getId());
    		for(Customer c: listCustomers) {
    			map.get(c.getId()).put(p.getId(), -1.0);
    		}
    	}
    	
    	for(Review r: listReviews) {
    		map.get(r.getCustomer().getId()).put(r.getProduct().getId(), (double)r.getRating());
    	}
    	
        generateSimilarityMatrix(itemList, customerList, simMap, map);
    }

	private void generateSimilarityMatrix(
            ArrayList<Integer> itemList,
            ArrayList<Integer> customerList,
            HashMap<Integer, HashMap<Integer, Double>> simMap,
            HashMap<Integer, HashMap<Integer, Double>> map
    ) {
        double temp;
        int item1;
        int item2;
        for (int x = 0; x < itemList.size(); ++x) {
            item1 = itemList.get(x);
            simMap.put(item1, new HashMap<>());
            for (int y = 0; y < itemList.size(); ++y) {
                item2 = itemList.get(y);
                if (x == y) {
                    simMap.get(item1).put(itemList.get(y), 1.0);
                } else {
                    if (x < y) {
                        temp = getSimilarValue(map, customerList, item1, item2);
                        simMap.get(item1).put(item2, temp);
                    } else {
                        temp = simMap.get(item2).get(item1);
                        simMap.get(item1).put(item2, temp);
                    }
                }
            }
        }
    }

	private double getSimilarValue(
            HashMap<Integer, HashMap<Integer, Double>> map,
            ArrayList<Integer> customerList,
            Integer item1,
            Integer item2
    ) {
        ArrayList<Double> item1Score = new ArrayList<>();
        ArrayList<Double> item2Score = new ArrayList<>();
        Double score1;
        Double score2;
        for (Integer customerId : customerList) {
            score1 = map.get(customerId).get(item1);
            score2 = map.get(customerId).get(item2);
            if ((score1 != -1.0) && (score2 != -1.0)) {
                item1Score.add(score1);
                item2Score.add(score2);
            }
        }
        return findSimilarity(item1Score, item2Score);
    }

	private double findSimilarity(ArrayList<Double> arr1, ArrayList<Double> arr2) {
    	if(arr1.isEmpty() || arr2.isEmpty()) return 0.0;
        double value = multiplyVector(arr1, arr2) / (findDistance(arr2) * findDistance(arr1));
        return value;
    }

	private double multiplyVector(ArrayList<Double> arr1, ArrayList<Double> arr2) {
        double result = 0;
        int len = arr1.size();
        if (len > arr2.size()) {
            len = arr2.size();
        }
        for (int i = 0; i < len; ++i) {
            result += arr1.get(i) * arr2.get(i);
        }
        return result;
    }

	private double findDistance(ArrayList<Double> arr) {
        float result = 0;
        for (int i = 0; i < arr.size(); ++i) {
            result += arr.get(i) * arr.get(i);
        }
        return Math.sqrt(result);
    }

	private double predict(ArrayList<Double> score, ArrayList<Double> sim) {
        double a = 0, b = 0;
        int len = score.size();
        if (len > sim.size()) {
            len = sim.size();
        }
        for (int i = 0; i < len; ++i) {
            a += score.get(i) * sim.get(i);
            b += sim.get(i);
        }
        if (b <= 0) {
            return 0;
        }
        return a / b;
    }
	
}