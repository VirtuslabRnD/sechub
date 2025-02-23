// SPDX-License-Identifier: MIT
package com.mercedesbenz.sechub.test;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class SecHubTestURLBuilder extends AbstractTestURLBuilder {

    private static final String API_ADMIN = "/api/admin";
    private static final String API_ANONYMOUS = "/api/anonymous";

    private static final String API_ADMIN_USER = API_ADMIN + "/user";
    private static final String API_ADMIN_PROJECT = API_ADMIN + "/project";
    private static final String API_ADMIN_JOB = API_ADMIN + "/job";
    private static final String API_ADMIN_JOBS = API_ADMIN_JOB + "s";
    private static final String API_ADMIN_SCHEDULER = API_ADMIN + "/scheduler";

    private static final String API_ADMIN_SCAN = API_ADMIN + "/scan";
    private static final String API_ADMIN_CONFIG = API_ADMIN + "/config";
    private static final String API_ADMIN_CONFIG_MAPPING = API_ADMIN_CONFIG + "/mapping";
    private static final String API_PROJECT = "/api/project";

    public static SecHubTestURLBuilder https(int port) {
        return new SecHubTestURLBuilder("https", port);
    }

    public static SecHubTestURLBuilder http(int port) {
        return new SecHubTestURLBuilder("http", port);
    }

    public SecHubTestURLBuilder(String protocol, int port) {
        this(protocol, port, "localhost");
    }

    public SecHubTestURLBuilder(String protocol, int port, String hostname) {
        super(protocol, port, hostname);
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ CLI /Execute .............................+ */
    /* +-----------------------------------------------------------------------+ */
    public String buildAddJobUrl(String projectId) {
        return buildUrl(API_PROJECT, projectId, "job");
    }

    public String buildApproveJobUrl(String projectId, String jobUUID) {
        return buildUrl(API_PROJECT, projectId, "job", jobUUID, "approve");
    }

    public String buildGetJobStatusUrl(String projectId, String jobUUID) {
        return buildUrl(API_PROJECT, projectId, "job", jobUUID);
    }

    public String buildGetJobReportUrl(String projectId, UUID jobUUID) {
        return buildGetJobReportUrl(projectId, jobUUID.toString());
    }

    public String buildGetJobReportUrl(String projectId, String jobUUID) {
        return buildUrl(API_PROJECT, projectId, "report", jobUUID);
    }

    public String buildGetJobReportUrlSpdx(String projectId, UUID jobUUID) {
        return buildGetJobReportUrlSpdx(projectId, jobUUID.toString());
    }

    public String buildGetJobReportUrlSpdx(String projectId, String jobUUID) {
        return buildUrl(API_PROJECT, projectId, "report", "spdx", jobUUID);
    }

    public String buildUploadSourceCodeUrl(String projectId, UUID jobUUID) {
        return buildUploadSourceCodeUrl(projectId, jobUUID.toString());
    }

    public String buildUploadSourceCodeUrl(String projectId, String jobUUID) {
        return buildUrl(API_PROJECT, projectId, "job", jobUUID, "sourcecode");
    }

    public String buildUploadBinariesUrl(String projectId, UUID jobUUID) {
        return buildUploadBinariesUrl(projectId, jobUUID.toString());
    }

    public String buildUploadBinariesUrl(String projectId, String jobUUID) {
        return buildUrl(API_PROJECT, projectId, "job", jobUUID, "binaries");
    }

    public String buildUserAddsFalsePositiveJobDataListForProject(String projectId) {
        return buildUrl(API_PROJECT, projectId, "false-positives");
    }

    public String buildUserRemovesFalsePositiveEntryFromProject(String projectId, String jobUUID, String findingId) {
        return buildUrl(API_PROJECT, projectId, "false-positive", jobUUID, findingId);
    }

    public String buildUserFetchesFalsePositiveConfigurationOfProject(String projectId) {
        return buildUrl(API_PROJECT, projectId, "false-positives");
    }

    public String buildUserFetchesListOfJobsForProject(String projectId, String size, String page) {

        String url = appendParameters(buildUrl(API_PROJECT, projectId, "jobs"), params().set("size", size).set("page", page).build());
        return url;
    }

    private static ParameterBuilder params() {
        return new ParameterBuilder();
    }

    private static class ParameterBuilder {

        private Map<String, String> map = new LinkedHashMap<>();

        public Map<String, String> build() {
            return map;
        }

        public ParameterBuilder set(String key, String value) {
            if (value != null) {
                map.put(key, value);
            }
            return this;
        }
    }

    private String appendParameters(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (params == null || params.isEmpty()) {
            return url;
        }
        sb.append("?");

        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            String value = params.get(name);
            sb.append(name);
            sb.append('=');
            sb.append(value);
            if (it.hasNext()) {
                sb.append('&');
            }
        }
        return url + sb.toString();
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ anonymous ................................+ */
    /* +-----------------------------------------------------------------------+ */

    public String buildUserSignUpUrl() {
        return buildUrl(API_ANONYMOUS, "signup");
    }

    public String buildAnonymousGetNewApiTokenByLinkWithOneTimeTokenUrl(String oneTimeToken) {
        return buildUrl(API_ANONYMOUS, "apitoken", oneTimeToken);
    }

    public String buildAnonymousRequestNewApiToken(String emailAddress) {
        return buildUrl(API_ANONYMOUS, "refresh/apitoken", emailAddress);
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/signup .............................+ */
    /* +-----------------------------------------------------------------------+ */

    public String buildAdminAcceptsUserSignUpUrl(String userId) {
        return buildUrl(API_ADMIN, "signup/accept", userId);
    }

    public String buildAdminDeletesUserSignUpUrl(String userId) {
        return buildUrl(API_ADMIN, "signup", userId);
    }

    public String buildAdminListsUserSignupsUrl() {
        return buildUrl(API_ADMIN, "signups");
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/users ..............................+ */
    /* +-----------------------------------------------------------------------+ */

    public String buildDeleteUserUrl(String userId) {
        return buildUrl(API_ADMIN_USER, userId);
    }

    public String buildGetUserDetailsUrl(String userId) {
        return buildUrl(API_ADMIN_USER, userId);
    }

    public String buildAdminListsUsersUrl() {
        return buildUrl(API_ADMIN, "users");
    }

    public String buildAdminListsAdminsUrl() {
        return buildUrl(API_ADMIN, "admins");
    }

    public String buildAdminDeletesUserUrl(String userId) {
        return buildUrl(API_ADMIN_USER, userId);
    }

    public String buildAdminShowsUserDetailsUrl(String userId) {
        return buildUrl(API_ADMIN_USER, userId);
    }

    public String buildAdminChangesUserEmailAddress(String userId, String newEmailAddress) {
        return buildUrl(API_ADMIN_USER, userId, "email", newEmailAddress);
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/projects ...........................+ */
    /* +-----------------------------------------------------------------------+ */

    public String buildDeleteProjectUrl(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId);
    }

    public String buildAdminCreatesProjectUrl() {
        return buildUrl(API_ADMIN_PROJECT);
    }

    public String buildAdminGetProjectDetailsUrl(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId);
    }

    public String buildAdminListsProjectsUrl() {
        return buildUrl(API_ADMIN, "projects");
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ project ..................................+ */
    /* +-----------------------------------------------------------------------+ */

    public String buildFetchJobStatus(String projectId, String jobUUID) {
        return buildUrl(API_PROJECT, projectId, "job", jobUUID);
    }

    public String buildGetFalsePositiveConfigurationOfProject(String projectId) {
        return buildUrl(API_PROJECT, projectId, "false-positives");
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/projects ...........................+ */
    /* +-----------------------------------------------------------------------+ */
    public String buildAdminShowsProjectDetailsUrl(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId);
    }

    public String buildAdminChangesProjectDescriptionUrl(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId);
    }

    public String buildAdminChangesProjectOwnerUrl(String projectId, String userId) {
        return buildUrl(API_ADMIN_PROJECT, projectId, "owner", userId);
    }

    public String buildAdminAssignsUserToProjectUrl(String projectId, String userId) {
        return buildUrl(API_ADMIN_PROJECT, projectId, "membership", userId);
    }

    public String buildAdminUnassignsUserFromProjectUrl(String projectId, String userId) {
        return buildUrl(API_ADMIN_PROJECT, projectId, "membership", userId);
    }

    public String buildAdminFetchProjectInfoUrl(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId);
    }

    public String buildUpdateProjectWhiteListUrl(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId, "whitelist");
    }

    public String buildUpdateProjectMetaData(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId, "metadata");
    }

    public String buildAdminDeletesProject(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId);
    }

    public String buildAdminFetchesScanLogsForProject(String projectId) {
        return buildUrl(API_ADMIN_PROJECT, projectId, "scan", "logs");
    }

    public String buildAdminChangesProjectAccessLevelUrl(String projectId, String projectAccessLevel) {
        return buildUrl(API_ADMIN_PROJECT, projectId, "accesslevel", projectAccessLevel);
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/jobs ...............................+ */
    /* +-----------------------------------------------------------------------+ */
    public String buildAdminFetchAllRunningJobsUrl() {
        return buildUrl(API_ADMIN_JOBS, "running");
    }

    public String buildAdminDownloadsZipFileContainingFullScanDataFor(UUID sechubJobUUID) {
        return buildAdminDownloadsZipFileContainingFullScanDataFor(sechubJobUUID.toString());
    }

    public String buildAdminDownloadsZipFileContainingFullScanDataFor(String sechubJobUUID) {
        return buildUrl(API_ADMIN_SCAN, "download", sechubJobUUID);
    }

    public String buildAdminCancelsJob(UUID jobUUID) {
        return buildAdminCancelsJob(jobUUID.toString());
    }

    public String buildAdminCancelsJob(String jobUUID) {
        return buildUrl(API_ADMIN_JOBS, "cancel", jobUUID);
    }

    public String buildAdminRestartsJob(UUID jobUUID) {
        return buildAdminRestartsJob(jobUUID.toString());
    }

    public String buildAdminRestartsJob(String jobUUID) {
        return buildUrl(API_ADMIN_JOBS, "restart", jobUUID);
    }

    public String buildAdminRestartsJobHard(UUID jobUUID) {
        return buildAdminRestartsJobHard(jobUUID.toString());
    }

    public String buildAdminRestartsJobHard(String jobUUID) {
        return buildUrl(API_ADMIN_JOBS, "restart-hard", jobUUID);
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/scheduler/..........................+ */
    /* +-----------------------------------------------------------------------+ */
    public String buildAdminDisablesSchedulerJobProcessing() {
        return buildUrl(API_ADMIN_SCHEDULER, "disable/job-processing");
    }

    public String buildAdminEnablesSchedulerJobProcessing() {
        return buildUrl(API_ADMIN_SCHEDULER, "enable/job-processing");
    }

    public String buildAdminTriggersRefreshOfSchedulerStatus() {
        return buildUrl(API_ADMIN_SCHEDULER, "status", "refresh");
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/status/.............................+ */
    /* +-----------------------------------------------------------------------+ */
    public String buildAdminListsStatusEntries() {
        return buildUrl(API_ADMIN, "status");
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/admin...............................+ */
    /* +-----------------------------------------------------------------------+ */
    public String buildAdminGrantsSuperAdminRightsTo(String targetUser) {
        return buildUrl(API_ADMIN_USER, targetUser, "grant", "superadmin");
    }

    public String buildAdminRevokesSuperAdminRightsFrom(String targetUser) {
        return buildUrl(API_ADMIN_USER, targetUser, "revoke", "superadmin");
    }
    /* +-----------------------------------------------------------------------+ */
    /* +............................ admin/config..............................+ */
    /* +-----------------------------------------------------------------------+ */

    public String buildAdminCreatesProductExecutionProfile(String profileId) {
        return buildUrl(API_ADMIN_CONFIG, "execution/profile", profileId);
    }

    public String buildAdminUpdatesProductExecutionProfile(String profileId) {
        return buildUrl(API_ADMIN_CONFIG, "execution/profile", profileId);
    }

    public String buildAdminAddsProjectToExecutionProfile(String profileId, String projectId) {
        return buildUrl(API_ADMIN_CONFIG, "execution/profile", profileId, "project", projectId);
    }

    public String buildAdminRemovesProjectFromExecutionProfile(String profileId, String projectId) {
        return buildUrl(API_ADMIN_CONFIG, "execution/profile", profileId, "project", projectId);
    }

    public String buildAdminFetchesProductExecutionProfile(String profileId) {
        return buildUrl(API_ADMIN_CONFIG, "execution/profile", profileId);
    }

    public String buildAdminDeletesProductExecutionProfile(String profileId) {
        return buildUrl(API_ADMIN_CONFIG, "execution/profile", profileId);
    }

    public String buildAdminFetchesListOfProductExecutionProfiles() {
        return buildUrl(API_ADMIN_CONFIG, "execution/profiles");
    }

    public String buildAdminCreatesProductExecutorConfig() {
        return buildUrl(API_ADMIN_CONFIG, "executor");
    }

    public String buildAdminFetchesProductExecutorConfig(String uuid) {
        return buildUrl(API_ADMIN_CONFIG, "executor", uuid);
    }

    public String buildAdminFetchesProductExecutorConfig(UUID uuid) {
        return buildAdminFetchesProductExecutorConfig(uuid.toString());
    }

    public String buildAdminUpdatesProductExecutorConfig(String uuid) {
        return buildUrl(API_ADMIN_CONFIG, "executor", uuid);
    }

    public String buildAdminUpdatesProductExecutorConfig(UUID uuid) {
        return buildAdminUpdatesProductExecutorConfig(uuid.toString());
    }

    public String buildAdminFetchesListOfProductExecutionConfigurations() {
        return buildUrl(API_ADMIN_CONFIG, "executors");
    }

    public String buildAdminDeletesProductExecutorConfig(String uuid) {
        return buildUrl(API_ADMIN_CONFIG, "executor", uuid);
    }

    public String buildAdminDeletesProductExecutorConfig(UUID uuid) {
        return buildAdminDeletesProductExecutorConfig(uuid.toString());
    }

    public String buildAdminUpdatesAutoCleanupConfigurationUrl() {
        return buildUrl(API_ADMIN_CONFIG, "autoclean");
    }

    public String buildAdminFetchesAutoCleanupConfigurationUrl() {
        return buildUrl(API_ADMIN_CONFIG, "autoclean");
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ integration test special (anonymous) .....+ */
    /* +-----------------------------------------------------------------------+ */
    public String buildIntegrationtTestFetchAllPDSJobUUIDSForSecHubJob(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/job", sechubJobUUID, "pds/uuids");
    }

    public String buildCountProjectScanAccess(String projectId) {
        return buildUrl(API_ANONYMOUS, "integrationtest/project/" + projectId + "/scan/access/count");
    }

    public String buildCountProjectScheduleAccess(String projectId) {
        return buildUrl(API_ANONYMOUS, "integrationtest/project/" + projectId + "/schedule/access/count");
    }

    public String buildCountProjectProductResults(String projectId) {
        return buildUrl(API_ANONYMOUS, "integrationtest/project/" + projectId + "/scan/productresult/count");
    }

    public String buildFetchAllProjectProductResultsButShrinked(String projectId, int maxLength) {
        return buildUrl(API_ANONYMOUS, "integrationtest/project/" + projectId + "/scan/productresult/all-shrinked/" + maxLength);
    }

    public String buildCountProjectScanReports(String projectId) {
        return buildUrl(API_ANONYMOUS, "integrationtest/project/" + projectId + "/scan/report/count");
    }

    public String buildFetchEmailsFromMockMailServiceUrl(String emailAdress) {
        return buildUrl(API_ANONYMOUS, "integrationtest/mock/emails/to", emailAdress);
    }

    public String buildResetAllMockMailsUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/mock/emails");
    }

    public String buildSetSchedulerStrategyIdUrl(String strategyId) {
        return buildUrl(API_ANONYMOUS, "integrationtest/scheduler/strategy/" + strategyId);
    }

    /**
     * Integration test only URL!
     *
     * @return url for integration test check
     */
    public String buildIntegrationTestIsAliveUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/alive");
    }

    public String buildCheckIsAliveUrl() {
        return buildUrl(API_ANONYMOUS, "check/alive");
    }

    public String buildGetFileUpload(String projectId, String jobUUID, String fileName) {
        return buildUrl(API_ANONYMOUS, "integrationtest/" + projectId + "/" + jobUUID + "/uploaded/" + fileName);
    }

    public String buildGetServerVersionUrl() {
        return buildUrl(API_ADMIN, "info/version");
    }

    /* +-----------------------------------------------------------------------+ */
    /* +............................ integration test special (from user) .....+ */
    /* +-----------------------------------------------------------------------+ */

    public String buildIntegrationTestLogInfoUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/log/info");
    }

    public String buildCheckRole(String role) {
        return buildUrl(API_ANONYMOUS, "integrationtest/check/role/", role);
    }

    public String buildFetchReport(String projectId, UUID sechubJobUUID) {
        return buildUrl(API_PROJECT, projectId, "report", sechubJobUUID.toString());
    }

    public String buildIntegrationTestChangeMappingDirectlyURL(String mappingId) {
        return buildUrl(API_ANONYMOUS, "integrationtest/config/scan/mapping/", mappingId);
    }

    public String buildIntegrationTestFetchMappingDirectlyURL(String mappingId) {
        return buildUrl(API_ANONYMOUS, "integrationtest/config/scan/mapping/", mappingId);
    }

    public String buildIntegrationTestGetIdForNameByNamePatternProvider(String namePatternProviderId, String name) {
        return buildUrl(API_ANONYMOUS, "integrationtest/config/namepattern", namePatternProviderId, name);
    }

    public String buildClearMetaDataInspectionURL() {
        return buildUrl(API_ANONYMOUS, "integrationtest/metadata/inspections");
    }

    public String buildFetchMetaDataInspectionsURL() {
        return buildUrl(API_ANONYMOUS, "integrationtest/metadata/inspections");
    }

    public String buildSetProjectMockConfiguration(String projectId) {
        return buildUrl(API_PROJECT, projectId, "mockdata");
    }

    public String buildGetProjectMockConfiguration(String projectId) {
        return buildUrl(API_PROJECT, projectId, "mockdata");
    }

    public String buildUpdateMapping(String mappingId) {
        return buildUrl(API_ADMIN_CONFIG_MAPPING, mappingId);
    }

    public String buildGetMapping(String mappingId) {
        return buildUrl(API_ADMIN_CONFIG_MAPPING, mappingId);
    }

    public String buildIntegrationTestResetAndStopEventInspection() {
        return buildUrl(API_ANONYMOUS, "integrationtest/event/inspection/reset-and-stop");
    }

    public String buildIntegrationTestStartEventInspection() {
        return buildUrl(API_ANONYMOUS, "integrationtest/event/inspection/start");
    }

    public String buildIntegrationTestFetchEventInspectionStatus() {
        return buildUrl(API_ANONYMOUS, "integrationtest/event/inspection/status");
    }

    public String buildIntegrationTestFetchEventInspectionHistory() {
        return buildUrl(API_ANONYMOUS, "integrationtest/event/inspection/history");
    }

    public String buildIntegrationTestDeleteAllWaitingJobsUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/jobs/waiting");
    }

    public String buildIntegrationTestCancelAllScanJobsUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/scan/cancel/jobs");
    }

    public String buildIntegrationTestRevertJobAsStillRunning(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/schedule/revert/job/" + sechubJobUUID.toString() + "/still-running");
    }

    public String buildIntegrationTestRevertJobAsStillNotApproved(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/schedule/revert/job/" + sechubJobUUID.toString() + "/still-not-approved");
    }

    public String buildIntegrationTestFakeProductResult(String projectId, UUID sechubJobUUID, String productIdentifier) {
        return buildUrl(API_ANONYMOUS, "integrationtest/project/" + projectId + "/job/" + sechubJobUUID + "/scan/productresult/" + productIdentifier);
    }

    public String buildintegrationTestDeleteProductResults(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/job/" + sechubJobUUID + "/productresults");
    }

    public String buildIntegrationTestCountProductResults(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/job/" + sechubJobUUID + "/productresults-count");
    }

    public String buildintegrationTestDeleteAllProductExecutorConfigurations() {
        return buildUrl(API_ANONYMOUS, "integrationtest/config/executors");
    }

    public String buildintegrationTestIsExecutionProfileExisting(String profileId) {
        return buildUrl(API_ANONYMOUS, "integrationtest/config/execution/profile/" + profileId + "/exists");
    }

    public String buildIntegrationTestClearSecurityLogs() {
        return buildUrl(API_ANONYMOUS, "integrationtest/logs/security");
    }

    public String buildIntegrationTestGetSecurityLogs() {
        return buildUrl(API_ANONYMOUS, "integrationtest/logs/security");
    }

    public String buildIntegrationTestFetchScheduleAutoCleanupDaysUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/autocleanup/inspection/schedule/days");
    }

    public String buildIntegrationTestFetchScanAutoCleanupDaysUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/autocleanup/inspection/scan/days");
    }

    public String buildIntegrationTestFetchAdministrationAutoCleanupDaysUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/autocleanup/inspection/administration/days");
    }

    public String buildIntegrationTestResetAutoCleanupInspectionUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/autocleanup/inspection/reset");
    }

    public String buildIntegrationTestFetchAutoCleanupInspectionDeleteCountsUrl() {
        return buildUrl(API_ANONYMOUS, "integrationtest/autocleanup/inspection/deleteCounts");
    }

    public String buildIntegrationTestFetchFullScandata(UUID sechubJobUIUD) {
        return buildUrl(API_ANONYMOUS, "integrationtest/job/" + sechubJobUIUD + "/fullscandata");
    }

    // statistic parts

    public String buildintegrationTestFetchJobStatistic(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/statistic/job/" + sechubJobUUID);
    }

    public String buildintegrationTestFetchJobStatisticData(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/statistic/job-data/" + sechubJobUUID);
    }

    public String buildintegrationTestFetchJobRunStatistic(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/statistic/job-run/" + sechubJobUUID);
    }

    public String buildintegrationTestFetchJobRunStatisticData(UUID sechubJobUUID) {
        return buildUrl(API_ANONYMOUS, "integrationtest/statistic/job-run-data/" + sechubJobUUID);
    }

}
