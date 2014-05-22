package auction.rest;

import auction.jms.IncomingBidsProducer;
import dao.BidDAO;
import dao.ProductDAO;
import entity.Bid;
import entity.Product;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * @author Evgenia
 */

@Path("/product")
@Produces("application/json")
public class ProductRestfulService {
    private ProductDAO productDAO;
    private BidDAO bidDAO;

    @GET
    @Path("/featured")
    public List<Product> getFeaturedProducts() {
        return productDAO.getFeaturedProducts();
    }

    @GET
    @Path("/search")
    public List<Product> getSearchProducts(@Context UriInfo uriInfo) {
        return productDAO.getSearchProducts(uriInfo.getQueryParameters());
    }

    @GET
    @Path("/{id}")
    public Product getProduct(@PathParam("id") int id) {
        return productDAO.getById(id);
    }

    @GET
    @Path("/{productId}/bid")
    public List<Bid> getProductBids(@PathParam("productId") int productId) {
        return bidDAO.getBidsByProduct(productId);
    }

    @Inject
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Inject
    public void setBidDAO(BidDAO bidDAO) {
        this.bidDAO = bidDAO;
    }
}
