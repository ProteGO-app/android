package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.usecase.covidtest.GetTestSubscriptionPinUseCase
import pl.gov.mc.protegosafe.domain.usecase.covidtest.GetTestSubscriptionStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.covidtest.UploadTestSubscriptionPinUseCase
import pl.gov.mc.protegosafe.domain.usecase.info.UpdateDashboardIfRequiredAndGetResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.info.UpdateDetailsIfRequiredAndGetResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.GetSubscribedDistrictsResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.GetVoivodeshipsResultOrFetchIfRequiredUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.GetVoivodeshipsResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.HandleDistrictActionUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.UpdateVoivodeshipsIfRequiredUseCase

class OnGetBridgeDataUseCase(
    private val getServicesStatusUseCase: GetServicesStatusUseCase,
    private val getAnalyzeResultUseCase: GetAnalyzeResultUseCase,
    private val getAppVersionNameUseCase: GetAppVersionNameUseCase,
    private val getSystemLanguageUseCase: GetSystemLanguageUseCase,
    private val getFontScaleUseCase: GetFontScaleUseCase,
    private val handleDistrictActionUseCase: HandleDistrictActionUseCase,
    private val getSubscribedDistrictsResultUseCase: GetSubscribedDistrictsResultUseCase,
    private val uploadTestSubscriptionPinUseCase: UploadTestSubscriptionPinUseCase,
    private val getTestSubscriptionStatusUseCase: GetTestSubscriptionStatusUseCase,
    private val getTestSubscriptionPinUseCase: GetTestSubscriptionPinUseCase,
    private val cancelExposureRiskUseCase: CancelExposureRiskUseCase,
    private val getActivitiesResultUseCase: GetActivitiesResultUseCase,
    private val getVoivodeshipsResultUseCase: GetVoivodeshipsResultUseCase,
    private val getVoivodeshipsResultOrFetchIfRequiredUseCase: GetVoivodeshipsResultOrFetchIfRequiredUseCase,
    private val updateVoivodeshipsIfRequiredUseCase: UpdateVoivodeshipsIfRequiredUseCase,
    private val updateCovidStatsNotificationsStatusUseCase: UpdateCovidStatsNotificationsStatusUseCase,
    private val getCovidStatsNotificationStatusResultUseCase: GetCovidStatsNotificationStatusResultUseCase,
    private val getENStatsResultUseCase: GetENStatsResultUseCase,
    private val updateDashboardIfRequiredAndGetResultUseCase: UpdateDashboardIfRequiredAndGetResultUseCase,
    private val updateDetailsIfRequiredAndGetResultUseCase: UpdateDetailsIfRequiredAndGetResultUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(
        type: OutgoingBridgeDataType,
        data: String?,
        requestId: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Single<String> {
        return when (type) {
            OutgoingBridgeDataType.SERVICES_STATUS -> {
                getServicesStatusUseCase.execute()
            }
            OutgoingBridgeDataType.ANALYZE_RESULT -> {
                getAnalyzeResultUseCase.execute()
            }
            OutgoingBridgeDataType.APP_VERSION -> {
                getAppVersionNameUseCase.execute()
            }
            OutgoingBridgeDataType.SYSTEM_LANGUAGE -> {
                getSystemLanguageUseCase.execute()
            }
            OutgoingBridgeDataType.GET_FONT_SCALE -> {
                getFontScaleUseCase.execute()
            }
            OutgoingBridgeDataType.EXPOSURE_RISK_CANCELLATION -> {
                cancelExposureRiskUseCase.execute()
            }
            OutgoingBridgeDataType.DISTRICTS_STATUS -> {
                getVoivodeshipsResultOrFetchIfRequiredUseCase.execute()
            }
            OutgoingBridgeDataType.UPDATE_DISTRICTS_STATUSES -> {
                updateVoivodeshipsIfRequiredUseCase.execute()
                    .andThen(getVoivodeshipsResultUseCase.execute())
            }
            OutgoingBridgeDataType.DISTRICT_ACTION -> {
                data?.let {
                    handleDistrictActionAndGetSubscribedDistrictResult(it)
                } ?: throw IllegalArgumentException()
            }
            OutgoingBridgeDataType.GET_SUBSCRIBED_DISTRICTS -> {
                getSubscribedDistrictsResultUseCase.execute()
            }
            OutgoingBridgeDataType.UPLOAD_COVID_TEST_PIN -> {
                data?.let {
                    uploadTestSubscriptionPinUseCase.execute(it, requestId)
                } ?: throw IllegalArgumentException()
            }
            OutgoingBridgeDataType.GET_COVID_TEST_SUBSCRIPTION_STATUS -> {
                getTestSubscriptionStatusUseCase.execute(onResultActionRequired)
            }
            OutgoingBridgeDataType.GET_COVID_TEST_SUBSCRIPTION_PIN -> {
                getTestSubscriptionPinUseCase.execute()
            }
            OutgoingBridgeDataType.GET_ACTIVITIES -> {
                getActivitiesResultUseCase.execute()
            }
            OutgoingBridgeDataType.GET_COVID_STATS -> {
                updateDashboardIfRequiredAndGetResultUseCase.execute()
            }
            OutgoingBridgeDataType.GET_COVID_STATS_NOTIFICATION_AGREEMENT -> {
                getCovidStatsNotificationStatusResultUseCase.execute()
            }
            OutgoingBridgeDataType.UPDATE_COVID_STATS_NOTIFICATION_AGREEMENT -> {
                data?.let {
                    updateCovidStatsNotificationsStatusUseCase.execute(it)
                } ?: throw IllegalArgumentException()
            }
            OutgoingBridgeDataType.GET_EN_STATS -> {
                getENStatsResultUseCase.execute()
            }
            OutgoingBridgeDataType.GET_DETAILS -> {
                updateDetailsIfRequiredAndGetResultUseCase.execute()
            }
            else -> {
                throw IllegalArgumentException("OutgoingBridgeDataType has wrong value")
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun handleDistrictActionAndGetSubscribedDistrictResult(payload: String): Single<String> {
        return handleDistrictActionUseCase.execute(payload)
            .andThen(getSubscribedDistrictsResultUseCase.execute())
    }
}
