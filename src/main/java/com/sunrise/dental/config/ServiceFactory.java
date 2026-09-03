package com.sunrise.dental.config;

import com.sunrise.dental.dao.AppointmentDao;
import com.sunrise.dental.dao.BillDao;
import com.sunrise.dental.dao.PatientDao;
import com.sunrise.dental.dao.PaymentDao;
import com.sunrise.dental.dao.ReportDao;
import com.sunrise.dental.dao.TreatmentChargeDao;
import com.sunrise.dental.dao.TreatmentDao;
import com.sunrise.dental.dao.TreatmentDetailDao;
import com.sunrise.dental.dao.UserDao;
import com.sunrise.dental.dao.impl.AppointmentDaoJdbc;
import com.sunrise.dental.dao.impl.BillDaoJdbc;
import com.sunrise.dental.dao.impl.PatientDaoJdbc;
import com.sunrise.dental.dao.impl.PaymentDaoJdbc;
import com.sunrise.dental.dao.impl.ReportDaoJdbc;
import com.sunrise.dental.dao.impl.TreatmentChargeDaoJdbc;
import com.sunrise.dental.dao.impl.TreatmentDaoJdbc;
import com.sunrise.dental.dao.impl.TreatmentDetailDaoJdbc;
import com.sunrise.dental.dao.impl.UserDaoJdbc;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.AuthService;
import com.sunrise.dental.service.BillService;
import com.sunrise.dental.service.PatientService;
import com.sunrise.dental.service.PaymentService;
import com.sunrise.dental.service.ReportService;
import com.sunrise.dental.service.TreatmentChargeService;
import com.sunrise.dental.service.TreatmentService;
import com.sunrise.dental.service.impl.AppointmentServiceImpl;
import com.sunrise.dental.service.impl.AuthServiceImpl;
import com.sunrise.dental.service.impl.BillServiceImpl;
import com.sunrise.dental.service.impl.PatientServiceImpl;
import com.sunrise.dental.service.impl.PaymentServiceImpl;
import com.sunrise.dental.service.impl.ReportServiceImpl;
import com.sunrise.dental.service.impl.TreatmentChargeServiceImpl;
import com.sunrise.dental.service.impl.TreatmentServiceImpl;
import com.sunrise.dental.util.DatabaseConnection;

public final class ServiceFactory {

    public static final String AUTH_SERVICE = "authService";
    public static final String PATIENT_SERVICE = "patientService";
    public static final String APPOINTMENT_SERVICE = "appointmentService";
    public static final String TREATMENT_CHARGE_SERVICE = "treatmentChargeService";
    public static final String TREATMENT_SERVICE = "treatmentService";
    public static final String BILL_SERVICE = "billService";
    public static final String PAYMENT_SERVICE = "paymentService";
    public static final String REPORT_SERVICE = "reportService";

    private ServiceFactory() {
    }

    public static void initialize(jakarta.servlet.ServletContext context) {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

        UserDao userDao = new UserDaoJdbc(databaseConnection);
        PatientDao patientDao = new PatientDaoJdbc(databaseConnection);
        AppointmentDao appointmentDao = new AppointmentDaoJdbc(databaseConnection);
        TreatmentChargeDao treatmentChargeDao = new TreatmentChargeDaoJdbc(databaseConnection);
        TreatmentDao treatmentDao = new TreatmentDaoJdbc(databaseConnection);
        TreatmentDetailDao treatmentDetailDao = new TreatmentDetailDaoJdbc(databaseConnection);
        BillDao billDao = new BillDaoJdbc(databaseConnection);
        PaymentDao paymentDao = new PaymentDaoJdbc(databaseConnection);
        ReportDao reportDao = new ReportDaoJdbc(databaseConnection);

        AuthService authService = new AuthServiceImpl(userDao);
        PatientService patientService = new PatientServiceImpl(patientDao);
        AppointmentService appointmentService = new AppointmentServiceImpl(appointmentDao, userDao);
        TreatmentChargeService treatmentChargeService = new TreatmentChargeServiceImpl(treatmentChargeDao);
        TreatmentService treatmentService = new TreatmentServiceImpl(treatmentDao, treatmentDetailDao, appointmentDao);
        BillService billService = new BillServiceImpl(billDao, treatmentDao, treatmentDetailDao);
        PaymentService paymentService = new PaymentServiceImpl(paymentDao, billDao);
        ReportService reportService = new ReportServiceImpl(reportDao, appointmentDao, billDao,
                patientDao, treatmentDao, paymentDao);

        context.setAttribute(AUTH_SERVICE, authService);
        context.setAttribute(PATIENT_SERVICE, patientService);
        context.setAttribute(APPOINTMENT_SERVICE, appointmentService);
        context.setAttribute(TREATMENT_CHARGE_SERVICE, treatmentChargeService);
        context.setAttribute(TREATMENT_SERVICE, treatmentService);
        context.setAttribute(BILL_SERVICE, billService);
        context.setAttribute(PAYMENT_SERVICE, paymentService);
        context.setAttribute(REPORT_SERVICE, reportService);
    }
}
