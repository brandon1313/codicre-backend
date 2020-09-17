package com.systemsolution.saving.service;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.systemsolution.lend.resource.PlanPaidResource;
import com.systemsolution.lend.service.LendService;
import com.systemsolution.saving.resource.SavingMovementRequest;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Service
public class PDFService {
	
	
	@Autowired
	DataSource dataSource;
	
	private static final String YYYY_MM_DD = "yyyy-MM-dd HH:mm:ss";
	
	@Autowired
	LendService lendService;

	public ByteArrayInputStream exportPdf(SavingMovementRequest movementRequest) throws IOException, JRException, SQLException, ParseException {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(movementRequest.getDateTo()); 
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD);
		String dateFromString = format.format(movementRequest.getDateFrom());
		  InputStream jrxmlInput = new ClassPathResource("stacta.jrxml").getInputStream();
          JasperDesign design = JRXmlLoader.load(jrxmlInput);
          JasperReport jasperReport = JasperCompileManager.compileReport(design);
          Map<String, Object> parameters = new HashMap<>();
          parameters.put("dateFrom",format.parse(dateFromString));
          parameters.put("dateUntil",calendar.getTime());
          parameters.put("saving_id",movementRequest.getSavingId());
          JasperPrint jasperPrint = JasperFillManager.fillReport(
        		  jasperReport, parameters, dataSource.getConnection());
		return new ByteArrayInputStream(writePdf(jasperPrint));
		
	}
	
	public ByteArrayInputStream exportPdfPlan(final Long lendId) throws IOException, JRException{
		
		InputStream jrxmlInput = new ClassPathResource("plain-paid.jrxml").getInputStream();
		JasperDesign design = JRXmlLoader.load(jrxmlInput);
		JasperReport jasperReport = JasperCompileManager.compileReport(design);
		Collection<PlanPaidResource> collection = new ArrayList<>();
        lendService.getPlanPaid(lendId).forEach(collection::add);
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(collection);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,null, beanColDataSource);
		return new ByteArrayInputStream(writePdf(jasperPrint));

	}
	
	private byte[] writePdf(JasperPrint jasperPrint) throws JRException {
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}
}
