package org.esa.beam.framework.gpf;

import com.bc.ceres.core.ProgressMonitor;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.gpf.internal.OperatorContext;

import java.awt.Rectangle;
import java.util.Map;
import java.util.logging.Logger;


/**
 * The abstract base class for all operators intended to be extended by clients.
 * <p>The following methods are intended to be implemented or overidden:
 * <ld>
 * <li>{@link #initialize()}: must be implemented in order to initialise the operator and create the target
 * product.</li>
 * <li>{@link #computeTile(org.esa.beam.framework.datamodel.Band,Tile) computeTile()}: implemented to compute the tile
 * for a single band.</li>
 * <li>{@link #computeTileStack(java.util.Map,java.awt.Rectangle) computeTileStack()}: implemented to compute the tiles
 * for multiple bands.</li>
 * <li>{@link #dispose()}: can be overidden in order to free all resources previously allocated by the operator.</li>
 * <li>{@link #getConfigurationConverter()}: can be overidden in order to return a suitable converter for an operator's configuration.</li>
 * </ld>
 * </p>
 * <p>Generally, only one {@code computeTile} method needs to be implemented. It depends on the type of algorithm which
 * of both operations is most advantageous to implement:
 * <ol>
 * <li>If bands can be computed independently of each other, then it is
 * beneficial to implement the {@code computeTile()} method. This is the case for sub-sampling, map-projections,
 * band arithmetic, band filtering and statistic analyses.</li>
 * <li>{@code computeTileStack()} should be overriden in cases where the bands of a product cannot be computed independently, e.g.
 * because they are a simultaneous output. This is often the case for algorithms based on neural network, cluster analyses,
 * model inversion methods or spectral unmixing.</li>
 * </ol>
 * </p>
 * <p>The framework execute either the {@code computeTile()} or the {@code computeTileStack()} method
 * based on the current use case or request.
 * If tiles for single bands are requested, e.g. for image display, it will always prefer an implementation of
 * the {@code computeTile()} method and call it.
 * If all tiles are requested at once, e.g. writing a product to disk, it will attempt to use the {@code computeTileStack()}
 * method. If the framework cannot use its preferred operation, it will use the one implemented by the operator.</p>
 *
 * @author Norman Fomferra
 * @author Marco Peters
 * @author Marco Zühlke
 * @since 4.1
 */
public abstract class Operator {

    final OperatorContext context;

    /**
     * Constructs a new operator.
     */
    protected Operator() {
        context = new OperatorContext(this);
    }

    /**
     * Initializes this operator and returns its one and only target product.
     * <p/>
     * <p>The framework calls this method after it has created this operator.
     * Any client code that must be performed before computation of tile data
     * should be placed here.</p>
     *
     * @return the target product
     * @throws OperatorException if an error occurs during operator initialisation
     * @see #getTargetProduct()
     */
    public abstract Product initialize() throws OperatorException;

    /**
     * Called by the framework in order to compute a tile for the given target band.
     * <p>The default implementation throws a runtime exception with the message "not implemented"</p>.
     *
     * @param targetBand the target band
     * @param targetTile the current tile associated with the target band to be computed
     * @throws OperatorException if an error occurs during computation of the target raster
     */
    public void computeTile(Band targetBand, Tile targetTile) throws OperatorException {
        throw new RuntimeException("not implemented (only Band supported)");
    }

    /**
     * Called by the framework in order to compute the stack of tiles for the given target bands.
     * <p>The default implementation throws a runtime exception with the message "not implemented"</p>.
     *
     * @param targetTiles     the current tiles to be computed for each target band
     * @param targetRectangle the area in pixel coordinates to be computed (same for all rasters in <code>targetRasters</code>)
     * @throws OperatorException if an error occurs during computation of the target rasters
     */
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle) throws OperatorException {
        throw new RuntimeException("not implemented");
    }

    /**
     * Releases the resources the operator has acquired during its lifetime.
     * The default implementation does nothing.
     */
    public void dispose() {
    }

    /**
     * Gets a suitable converter for this cperator's configuration.
     * This method is intended to be overridden by clients in order to provide
     * their own implementation of their configuration conversion.
     * The default implementation returns {@code this} if the derived operator
     * implements {@link org.esa.beam.framework.gpf.ParameterConverter}, or
     * {@code null} otherwise. In the latter case, a default configuration conversion
     * will be applied by the GPF.
     *
     * @return A suitable configuration converter or {@code null} for default configuration conversion.
     */
    public ParameterConverter getConfigurationConverter() {
        if (this instanceof ParameterConverter) {
            return (ParameterConverter) this;
        }
        return null;
    }

    /**
     * Gets the source product using the specified name.
     *
     * @param id the identifier
     * @return the source product, or {@code null} if not found
     */
    public final Product getSourceProduct(String id) {
        return context.getSourceProduct(id);
    }

    /**
     * Gets the source products in the order they have been declared.
     *
     * @return the array source products
     */
    public final Product[] getSourceProducts() {
        return context.getSourceProducts();
    }

    public final String getSourceProductId(Product product) {
        return context.getSourceProductId(product);
    }

    public final void setSpi(OperatorSpi operatorSpi) {
        context.setOperatorSpi(operatorSpi);
    }

    public final OperatorSpi getSpi() {
        return context.getOperatorSpi();
    }

    /**
     * Adds a product to the list of source products.
     * One product instance can be registered with different identifiers, e.g. "source", "source1" and "input"
     * in consecutive calls.
     *
     * @param id      a product identifier
     * @param product the product to be added
     */
    public final void addSourceProduct(String id, Product product) {
        context.addSourceProduct(id, product);
    }

    /**
     * Gets the target product for the operator.
     * <p/>
     * <p>If a target product has not been set so far, calling this method will result in a
     * call to {@link #initialize()}.</p>
     *
     * @return The target product.
     * @throws OperatorException Thrown by {@link #initialize()},
     *                           if the target product has not yet been created.
     */
    public final Product getTargetProduct() throws OperatorException {
        return context.getTargetProduct();
    }

    /**
     * Gets a {@link Tile} for a given band and rectangle.
     *
     * @param rasterDataNode the raster data node of a data product,
     *                       e.g. a {@link org.esa.beam.framework.datamodel.Band} or
     *                       {@link org.esa.beam.framework.datamodel.TiePointGrid}.
     * @param rectangle      the raster rectangle in pixel coordinates
     * @return a tile.
     * @throws OperatorException if the tile request cannot be processed
     */
    public final Tile getSourceTile(RasterDataNode rasterDataNode, Rectangle rectangle) throws OperatorException {
        return context.getSourceTile(rasterDataNode, rectangle);
    }


    /**
     * Returns true, if the computation should be canceled
     *
     * @return true if computation should be canceled
     */
    protected final boolean isCancellationRequested() {
        return context.isCancellationRequested();
    }

    /**
     * Return a progress monitor, that can be used by an operator
     * that want's to show progress
     *
     * @return a progress monitor
     */
    protected final ProgressMonitor createProgressMonitor() {
        return context.createProgressMonitor();
    }

    public final Map<String, Object> getParameters() {
        return context.getParameters();
    }

    public final void setParameters(Map<String, Object> parameters) {
        context.setParameters(parameters);
    }

    public final Xpp3Dom getConfiguration() {
        return context.getConfiguration();
    }

    public final void setConfiguration(Xpp3Dom configuration) {
        context.setConfiguration(configuration);
    }

    /**
     * Gets a logger to by used by the operator.
     *
     * @return a logger.
     */
    public final Logger getLogger() {
        return context.getLogger();
    }

    /**
     * Sets a logger for the operator.
     *
     * @param logger a logger.
     */
    public final void setLogger(Logger logger) {
        context.setLogger(logger);
    }
}
