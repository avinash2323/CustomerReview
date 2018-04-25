package de.hybris.platform.cusomterreview.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.customerreview.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * New class created in order to extend the features of the
 * DefaultCustomerReviewService.java.
 * 
 * Did not edit any of the classes present in the “customerreviewserver.jar.src”
 * folder.
 * 
 * @author avinashannamaneni
 *
 */
public class CustomerReviewServiceExtended extends AbstractBusinessService implements CustomerReviewService {

	private static Logger logger = LoggerFactory.getLogger(CustomerReviewServiceExtended.class.getName());

	/**
	 * getReviewsForProductWithinRange is used to read reviews for products within
	 * the given range.
	 * 
	 * @param product
	 * @param range
	 * @return List<CustomerReviewModel> of products within the given range.
	 */
	public List<CustomerReviewModel> getReviewsForProductWithinRange(ProductModel product, RangeModel range) {
		String query = "SELECT {" + Item.PK + "} FROM {" + "CustomerReview" + "} WHERE {" + "product"
				+ "}=?product AND ({" + "range" + "} BETWEEN {" + "rangeStart" + "}=?rangeStart AND {" + "rangeEnd"
				+ "}=?rangeEnd) ORDER BY {" + "creationtime" + "} DESC";

		logger.debug("getReviewForProductWithinRange query - " + query);
		FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query);
		fsQuery.addQueryParameter("product", product);
		fsQuery.addQueryParameter("rangeStart", range.getStartPoint());
		fsQuery.addQueryParameter("rangeEnd", range.getEndPoint());
		fsQuery.setResultClassList(Collections.singletonList(CustomerReviewModel.class));

		SearchResult<CustomerReviewModel> searchResult = getFlexibleSearchService().search(fsQuery);
		return searchResult.getResult();
	}

	/**
	 * validateCustomerReview method is used to create validate the customerReview
	 * for cursewords.
	 * 
	 * @param rating
	 * @param headline
	 * @param comment
	 * 
	 * @return true - if customerReviwew and rating validation doesn't have any
	 *         problems.
     *
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CustomException
     *
	 */
	public Boolean validateCustomerReview(Double rating, String comment)
			throws FileNotFoundException, IOException, CustomException {

		Boolean isCommentValid = this.validateComments(comment);
		Boolean isRatingValid = this.validateRating(rating);

		if (isCommentValid & isRatingValid == true) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * validateComments is used to validate the comment aganist a set of cursewords.
	 * 
	 * @param comment
	 * @return - true if there is no match. false if there is a match.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CustomException
	 */
	private static Boolean validateComments(String comment) throws FileNotFoundException, IOException, CustomException {

		Boolean isCommentValid = false;
		String cursewords = null;
		FileReader reader;
		BufferedReader input;

		// Read file containing curse words.
		reader = new FileReader("cursewords.txt");
		input = new BufferedReader(reader);

		// Check the "comment" string aganist the curse words, if you find any match
		// throw a custom error;
		// if there is no match then give a green light for creation of review.
		while ((cursewords = input.readLine()) != null) {
			if (comment.contains(cursewords)) {
				throw new CustomException("Cursewords found in the comments.");
			}
		}

		// isCommentValid is set to true, if the workflow reaches this point without throwing any exception.
		isCommentValid = true;

		return isCommentValid;
	}

	/**
	 * validateRating is used to validate the rating.
	 * 
	 * @param rating
	 * @return - true if rating is greater than 0. 
	 * 			 false if rating is less than 0.
	 * 
	 * @throws CustomException
	 */
	private static Boolean validateRating(Double rating) throws CustomException {

		Boolean isRatingValid = false;

		if (Double.compare(rating, Double.valueOf(0.0)) >= 0) {
			isRatingValid = true;
			logger.debug("isRatingValid  - " + isRatingValid);
		} else {
			throw new CustomException("Rating is invalid.");
		}

		return isRatingValid;
	}

}
