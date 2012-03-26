/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Justin Swanson
 */
public class LAreaChart extends LChart {

    public XYSeries lowTier;
    public XYSeries normTier;
    public XYSeries highTier;
    public XYSeries epicTier;
    XYPlot plot;
    XYSeriesCollection collection;
    Color PClevelColor = Color.lightGray; //new Color(50, 180, 180);

    public LAreaChart(String title_, Dimension size_, Color c) {
        super(title_, size_);

        collection = new XYSeriesCollection(lowTier);

        lowTier = new XYSeries("Low Tier");
        collection.addSeries(lowTier);

        normTier = new XYSeries("Norm Tier");
        collection.addSeries(normTier);

        highTier = new XYSeries("High Tier");
        collection.addSeries(highTier);

        epicTier = new XYSeries("Epic Tier");
        collection.addSeries(epicTier);

        chart = ChartFactory.createXYAreaChart(
                null,
                "Level Difference", "Chance to Spawn",
                collection,
                PlotOrientation.VERTICAL,
                false, // legend
                true, // tool tips
                false // URLs
                );

        plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundAlpha(0.0f);
        plot.setForegroundAlpha(0.70f);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.getDomainAxis().setLabelPaint(PClevelColor);
        plot.getRangeAxis().setLabelPaint(Color.lightGray);
        plot.getRangeAxis().setTickLabelsVisible(false);
        plot.getDomainAxis().setTickLabelPaint(Color.lightGray);
        plot.getDomainAxis().setAutoRange(false);
        plot.getDomainAxis().setRange(-15, 15);

        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setOutlineVisible(false);
        plot.getRangeAxis().setAxisLineVisible(false);
        plot.getRangeAxis().setTickMarksVisible(false);

        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(59, 196, 63));
        renderer.setSeriesPaint(1, new Color(70, 130, 180));
        renderer.setSeriesPaint(2, new Color(196,148,59));
        renderer.setSeriesPaint(3, new Color(196,59,59));

        init(title_, size_, c);
        cPanel.setSize(cPanel.getWidth() - 12, cPanel.getHeight());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(50, 180, 180));
        g.drawLine(this.getSize().width / 2 + 4,
                30,
                this.getSize().width / 2 + 4,
                this.getSize().height - 36);
    }

    public void clear() {
        lowTier.clear();
        normTier.clear();
        highTier.clear();
        epicTier.clear();
    }

    public void resetDomain() {
        Range range = plot.getDataRange(plot.getDomainAxis());
        double set = range.getLowerBound();
        if (range.getUpperBound() > Math.abs(set))
            set = range.getUpperBound();
        if (Math.abs(set) < 15)
            set = 15;
        plot.getDomainAxis().setRange(-Math.abs(set), Math.abs(set));
    }
}
