package com.example.ReferEase.Controller;

import com.example.ReferEase.Model.Users;
import com.example.ReferEase.Service.ReferralsService;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.security.Principal;
import java.util.List;

@RestController
public class ReferralsController {

    @Autowired
    ReferralsService referralsService;

    // Existing endpoint for getting referrals of the logged-in user
    @GetMapping("/user/getReferrals")
    public List<Users> getReferrals(Principal principal){
        return referralsService.getReferrals(principal);
    }



    @GetMapping("/user/generateReferralReport")
    public ResponseEntity<byte[]> generateReferralReport() {
        List<Users> referrals = referralsService.getAllReferrals(); // Ensure this returns the data

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Updated header to include Referral Code and Referred By
            String[] header = {"User ID", "Username", "Email", "Profile Status", "Referral Code", "Referred By"};
            csvWriter.writeNext(header);

            for (Users referral : referrals) {
                String profileStatus = referral.getIsProfileCompleted() ? "Completed" : "Pending";
                String referredBy = referral.getReferrer() != null ? referral.getReferrer().getUsername() : "N/A"; // Assuming referredBy is a User object
                String[] data = {
                        String.valueOf(referral.getId()),
                        referral.getUsername(),
                        referral.getEmail(),
                        profileStatus,
                        referral.getReferralCode(), // Referral code
                        referredBy // Referred by
                };
                csvWriter.writeNext(data);
            }

            // Explicitly flush the writer to ensure all data is written to the output stream
            writer.flush();

            // Debugging: Log the generated CSV content
            String csvContent = new String(outputStream.toByteArray());
            System.out.println("Generated CSV Content: ");
            System.out.println(csvContent);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=referral_report.csv");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating CSV".getBytes());
        }
    }


}
