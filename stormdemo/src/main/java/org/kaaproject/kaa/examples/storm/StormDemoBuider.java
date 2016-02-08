/*
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.examples.storm;


import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.logs.LogAppenderDto;
import org.kaaproject.kaa.common.dto.logs.LogSchemaDto;
import org.kaaproject.kaa.examples.common.AbstractDemoBuilder;
import org.kaaproject.kaa.examples.common.KaaDemoBuilder;
import org.kaaproject.kaa.server.common.admin.AdminClient;
import org.kaaproject.kaa.server.common.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@KaaDemoBuilder
public class StormDemoBuider extends AbstractDemoBuilder {


    private static final Logger LOG = LoggerFactory.getLogger(StormDemoBuider.class);

    public StormDemoBuider() {
        super("demo/storm");
    }

    @Override
    protected void buildDemoApplicationImpl(AdminClient client) throws Exception {

        LOG.info("Loading 'Storm data analytics demo application' data...");

        loginTenantAdmin(client);

        ApplicationDto sparkApplication = new ApplicationDto();
        sparkApplication.setName("Storm data analytics demo");
        sparkApplication = client.editApplication(sparkApplication);

        sdkProfileDto.setApplicationId(sparkApplication.getId());
        sdkProfileDto.setApplicationToken(sparkApplication.getApplicationToken());
        sdkProfileDto.setProfileSchemaVersion(0);
        sdkProfileDto.setConfigurationSchemaVersion(1);
        sdkProfileDto.setNotificationSchemaVersion(1);

        loginTenantDeveloper(client);

        LogSchemaDto powerReportLogSchemaDto = new LogSchemaDto();
        powerReportLogSchemaDto.setApplicationId(sparkApplication.getId());
        powerReportLogSchemaDto.setName("Power report");
        powerReportLogSchemaDto.setDescription("Spark demo log schema");
        powerReportLogSchemaDto = client.createLogSchema(powerReportLogSchemaDto, getResourcePath("powerReportLogSchema.json"));
        sdkProfileDto.setLogSchemaVersion(powerReportLogSchemaDto.getVersion());

        LogAppenderDto flumeLogAppender = new LogAppenderDto();
        flumeLogAppender.setName("Storm data analytics demo log appender");
        flumeLogAppender.setDescription("Storm data analytics demo log appender");
        flumeLogAppender.setApplicationId(sparkApplication.getId());
        flumeLogAppender.setApplicationToken(sparkApplication.getApplicationToken());
        flumeLogAppender.setTenantId(sparkApplication.getTenantId());
        flumeLogAppender.setMinLogSchemaVersion(1);
        flumeLogAppender.setMaxLogSchemaVersion(Integer.MAX_VALUE);
        flumeLogAppender.setConfirmDelivery(true);
        flumeLogAppender.setPluginTypeName("Flume");
        flumeLogAppender.setPluginClassName("org.kaaproject.kaa.server.appenders.flume.appender.FlumeLogAppender");
        flumeLogAppender.setJsonConfiguration(FileUtils.readResource(getResourcePath("flume_appender.json")));
        flumeLogAppender = client.editLogAppenderDto(flumeLogAppender);

        LOG.info("Finished loading 'Spark Demo Application' data.");
    }

}
