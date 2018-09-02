package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Hello implements RequestHandler<Map<String, String>, String> {


    public String handleRequest(Map<String, String> input, Context context) {
        String coin = input.get("coin");
        String currency = input.get("currency");
        String exchange= input.get("exchange");
        final XYSeries series = new XYSeries("Random Data");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<BitCoinResponse> resp = restTemplate.getForEntity("https://min-api.cryptocompare.com/data/histominute?fsym="+ coin +"&tsym="+currency+"&e="+exchange+"&limit=60", BitCoinResponse.class);
        int i = 0;
        for (CoinResponse coinResponse : resp.getBody().Data) {

            series.add((double) i, (double) Double.valueOf(coinResponse.close));
            i++;
        }

        final XYSeriesCollection data = new XYSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                //input.get("coin"),
                "Coin",
                " ",
                " ",
                data,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        Integer width = 300, height = 180;
        double high = series.getMaxY();
        double low = series.getMinY();
        Color base = new Color(39, 50, 56);
        Color accent = new Color(21, 150, 136);
        Color base2 = new Color(19, 130, 172);
        Color accent2 = new Color(170, 57, 237);
        Color line = new Color(96, 210, 196);

        //     Making the chart not look horrible
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();

//        plot.setRangeAxis();

//        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setAxisLinePaint(base);
        domainAxis.setTickMarkPaint(accent);
        domainAxis.setTickLabelPaint(accent);
        rangeAxis.setAxisLinePaint(base);
        rangeAxis.setTickMarkPaint(accent);
        rangeAxis.setTickLabelPaint(accent);

        plot.getRenderer().setSeriesPaint(0, line);

        plot.setBackgroundPaint(base); /// white
        chart.setBackgroundPaint(base);

        plot.setDomainGridlinePaint(base);
        plot.setRangeGridlinePaint(base);

        plot.setRangeGridlinePaint(base);
        plot.setOutlineVisible(false);
        plot.setFixedLegendItems(null);


        rangeAxis.setAutoTickUnitSelection(false);
        rangeAxis.setAutoRange(false);

        rangeAxis.setRangeWithMargins(low - 5, high + 5);

//        rangeAxis.setRange((low), (high));
        rangeAxis.setTickUnit(new NumberTickUnit((high - low)));


        plot.setAxisOffset(new RectangleInsets(0.0, 0.0, 0.0, 0.0));


        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setSize(width, height);
        chart.setTitle((String.valueOf(System.currentTimeMillis())) + "--" + resp.getBody().Data.size());
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream,
                    chart,
                    chartPanel.getWidth(),
                    chartPanel.getHeight());

            String encodedfile = new String(Base64.getEncoder().encode(outputStream.toByteArray()), "UTF-8");
            System.out.println(encodedfile);
            return encodedfile;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";


    }



}