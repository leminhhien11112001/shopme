package com.shopme.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Review;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.product.BrandRepository;
import com.shopme.product.ProductService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {
	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

	@Autowired private ReviewService reviewService;
	@Autowired private CustomerService customerService;
	@Autowired private ProductService productService;
	@Autowired private CategoryService categoryService;
	@Autowired private BrandRepository repoBrand;

	@GetMapping("/reviews")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	@GetMapping("/reviews/page/{pageNum}")
	public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
			@PathVariable(name = "pageNum") int pageNum, String keyword, String sortField, String sortDir) {
		Customer customer = getAuthenticatedCustomer(request);
		Page<Review> page = reviewService.listByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);
		List<Review> listReviews = page.getContent();

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/reviews");

		model.addAttribute("listReviews", listReviews);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);

		return "reviews/reviews_customer";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = customerService.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}

	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		try {
			Review review = reviewService.getByCustomerAndId(customer, id);
			model.addAttribute("review", review);

			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/ratings/{productAlias}/page/{pageNum}")
	public String listByProductByPage(Model model, @PathVariable(name = "productAlias") String productAlias,
			@PathVariable(name = "pageNum") int pageNum, String sortField, String sortDir) {

		Product product = null;

		try {
			product = productService.getProduct(productAlias);
		} catch (ProductNotFoundException ex) {
			return "error/404";
		}

		Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
		List<Review> listReviews = page.getContent();

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);
		model.addAttribute("pageTitle", "Reviews for " + product.getShortName());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("listReviews", listReviews);
		model.addAttribute("product", product);

		return "reviews/reviews_product";
	}

	@GetMapping("/ratings/{productAlias}")
	public String listByProductFirstPage(@PathVariable(name = "productAlias") String productAlias, Model model) {
		return listByProductByPage(model, productAlias, 1, "reviewTime", "desc");
	}

	@GetMapping("/write_review/product/{productId}")
	public String showViewForm(@PathVariable("productId") Integer productId, Model model, HttpServletRequest request) {

		Review review = new Review();

		Product product = null;

		try {
			product = productService.getProduct(productId);
		} catch (ProductNotFoundException ex) {
			return "error/404";
		}

		Customer customer = getAuthenticatedCustomer(request);
		boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());

		if (customerReviewed) {
			model.addAttribute("customerReviewed", customerReviewed);
		} else {
			boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());

			if (customerCanReview) {
				model.addAttribute("customerCanReview", customerCanReview);
			} else {
				model.addAttribute("NoReviewPermission", true);
			}
		}

		model.addAttribute("product", product);
		model.addAttribute("review", review);

		return "reviews/review_form";
	}

	@PostMapping("/post_review")
	public String saveReview(Model model, Review review, Integer productId, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);

		Product product = null;

		try {
			product = productService.getProduct(productId);
		} catch (ProductNotFoundException ex) {
			return "error/404";
		}

		review.setProduct(product);
		review.setCustomer(customer);

		Review savedReview = reviewService.save(review);

		model.addAttribute("review", savedReview);

		return "reviews/review_done";
	}
	
	
	@GetMapping("/testContent")
	public String test() {
		ArrayList<Product> recommendProducts = contentBase(2, 5);
		System.out.println("Recommend:-------------------------------------");
		for (Product p : recommendProducts) {
			System.out.println(
					p.getId() + " " + p.getName() + " " + p.getBrand().getId() + " " + p.getCategory().getId());
		}
		return "redirect:/";
	}

	public ArrayList<Product> contentBase(int userId, int numberOfProduct) {
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

	public void sortByPreference(ArrayList<Product> unknownItemList, HashMap<String, Double> weightMap) {
		HashMap<Integer, Double> predictedScores = new HashMap<>();

		double brandScore, categoryScore;
		for (Product p : unknownItemList) {
			brandScore = weightMap.get("b" + p.getBrand().getId());
			categoryScore = weightMap.get("c" + p.getCategory().getId());
			predictedScores.put(p.getId(), brandScore + categoryScore);
		}

        System.out.println();        
        
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
		
		for(Integer i: predictedScores.keySet()) {
			 System.out.println(i + " " + predictedScores.get(i) + "    ");
		}
		
	}

	public HashMap<String, Double> generateWeightMap(HashMap<Integer, Double> userItemsMap,
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

	public void getDataFromDataBase(int userId, List<Integer> brandList, List<Integer> categoryList,
			HashMap<Integer, Double> userItemsMap, List<Product> knownItemList,
			List<Product> unknownItemList) {
		List<Brand> listBrands = (List<Brand>) repoBrand.findAll();
		List<Category> listCategories = categoryService.listAllCategories();
		
		for(Brand b: listBrands) {
			brandList.add(b.getId());
		}
		
		for(Category c: listCategories) {
			categoryList.add(c.getId());
		}

		List<Review> listReviews = reviewService.findByCustomer(userId);
		ArrayList<Integer> knownItemIds = new ArrayList<>();
		
		for(Review r: listReviews) {
			userItemsMap.put(r.getProduct().getId(), (double) r.getRating());
			knownItemIds.add(r.getProduct().getId());
		}

		List<Product> itemList = productService.findAll();

		for (Product p : itemList) {
			if (knownItemIds.contains(p.getId())) {
				knownItemList.add(p);
			} else {
				unknownItemList.add(p);
			}
		}
	}
	
	@GetMapping("/testItem")
	public String testItem() {
		HashMap<Integer, HashMap<Integer, Double>> map = new HashMap<>();
        ArrayList<Integer> customerList = new ArrayList<>();
        ArrayList<Integer> itemList = new ArrayList<>();
        HashMap<Integer, HashMap<Integer, Double>> simMap = new HashMap<>();

        generateMatrix(map, customerList, itemList, simMap);

        ArrayList<Integer> recommendedItems = recommendItem(map, 5, simMap, 2);

        System.out.println("Recommended: ");
        for (Integer itemId : recommendedItems) {
            System.out.print(itemId + " ");
        }
        System.out.println();
        
		return "redirect:/";
	}
	
	public ArrayList<Integer> recommendItem(
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

    public ArrayList<Integer> fillInMissingValue(
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
        
        return missingItem;
    }

    public void generateMatrix(
            HashMap<Integer, HashMap<Integer, Double>> map,
            ArrayList<Integer> customerList,
            ArrayList<Integer> itemList,
            HashMap<Integer, HashMap<Integer, Double>> simMap
    ) {
    	List<Customer> listCustomers = customerService.findAll();
    	List<Product> listProducts = productService.findAll();
    	List<Review> listReviews = reviewService.findAll();
    	
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

    public void generateSimilarityMatrix(
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

    public double getSimilarValue(
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

    public double findSimilarity(ArrayList<Double> arr1, ArrayList<Double> arr2) {
    	if(arr1.isEmpty() || arr2.isEmpty()) return 0.0;
        double value = multiplyVector(arr1, arr2) / (findDistance(arr2) * findDistance(arr1));
        return value;
    }

    public double multiplyVector(ArrayList<Double> arr1, ArrayList<Double> arr2) {
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

    public double findDistance(ArrayList<Double> arr) {
        float result = 0;
        for (int i = 0; i < arr.size(); ++i) {
            result += arr.get(i) * arr.get(i);
        }
        return Math.sqrt(result);
    }

    public double predict(ArrayList<Double> score, ArrayList<Double> sim) {
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