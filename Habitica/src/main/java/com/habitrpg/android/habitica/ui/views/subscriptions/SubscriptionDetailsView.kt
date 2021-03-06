package com.habitrpg.android.habitica.ui.views.subscriptions


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.habitrpg.android.habitica.BuildConfig
import com.habitrpg.android.habitica.R
import com.habitrpg.android.habitica.extensions.inflate
import com.habitrpg.android.habitica.models.user.SubscriptionPlan
import com.habitrpg.android.habitica.ui.helpers.bindView

class SubscriptionDetailsView : LinearLayout {

    internal val subscriptionDurationTextView: TextView by bindView(R.id.subscriptionDurationTextView)
    internal val subscriptionStatusActive: TextView by bindView(R.id.subscriptionStatusActive)
    private val getSubscriptionStatusInactive: TextView by bindView(R.id.subscriptionStatusInactive)
    internal val paymentProcessorTextView: TextView by bindView(R.id.paymentProcessorTextView)
    internal val monthsSubscribedTextView: TextView by bindView(R.id.monthsSubscribedTextView)
    internal val gemCapTextView: TextView by bindView(R.id.gemCapTextView)
    internal val currentHourglassesTextView: TextView by bindView(R.id.currentHourglassesTextView)
    private val cancelSubscripnDescription: TextView by bindView(R.id.cancelSubscriptionDescription)
    internal val visitWebsiteButton: Button by bindView(R.id.visitWebsiteButton)

    private var plan: SubscriptionPlan? = null

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setupView()
    }

    constructor(context: Context) : super(context) {
        setupView()
    }

    private fun setupView() {
        inflate(R.layout.subscription_details, true)

        visitWebsiteButton.setOnClickListener { openSubscriptionWebsite() }
    }

    fun setPlan(plan: SubscriptionPlan) {
        this.plan = plan

        if (plan.isActive) {
            subscriptionStatusActive.visibility = View.VISIBLE
            getSubscriptionStatusInactive.visibility = View.GONE
        } else {
            subscriptionStatusActive.visibility = View.GONE
            getSubscriptionStatusInactive.visibility = View.VISIBLE
        }

        var duration: String? = null

        if (plan.planId != null) {
            if (plan.planId == SubscriptionPlan.PLANID_BASIC || plan.planId == SubscriptionPlan.PLANID_BASICEARNED) {
                duration = resources.getString(R.string.month)
            } else if (plan.planId == SubscriptionPlan.PLANID_BASIC3MONTH) {
                duration = resources.getString(R.string.three_months)
            } else if (plan.planId == SubscriptionPlan.PLANID_BASIC6MONTH || plan.planId == SubscriptionPlan.PLANID_GOOGLE6MONTH) {
                duration = resources.getString(R.string.six_months)
            } else if (plan.planId == SubscriptionPlan.PLANID_BASIC12MONTH) {
                duration = resources.getString(R.string.twelve_months)
            }
        }

        if (duration != null) {
            subscriptionDurationTextView.text = resources.getString(R.string.subscription_duration, duration)
        }

        paymentProcessorTextView.text = plan.paymentMethod

        if (plan.consecutive.count == 1) {
            monthsSubscribedTextView.text = resources.getString(R.string.one_month)
        } else {
            monthsSubscribedTextView.text = resources.getString(R.string.months, plan.consecutive.count)
        }
        gemCapTextView.text = (plan.consecutive.gemCapExtra + 25).toString()
        currentHourglassesTextView.text = plan.consecutive.trinkets.toString()

        if (plan.paymentMethod != null) {
            if (plan.paymentMethod == "Google") {
                cancelSubscripnDescription.setText(R.string.cancel_subscription_google_description)
                visitWebsiteButton.setText(R.string.open_in_store)
            } else {
                cancelSubscripnDescription.setText(R.string.cancel_subscription_notgoogle_description)
                visitWebsiteButton.setText(R.string.visit_habitica_website)
            }
        }
    }

    fun openSubscriptionWebsite() {
        if (plan?.paymentMethod != null) {
            val intent: Intent = if (plan?.paymentMethod == "Google") {
                Intent(Intent.ACTION_VIEW)
                        .setComponent(ComponentName("com.android.vending",
                                "com.google.android.finsky.activities.MainActivity"))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            } else {
                Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.BASE_URL + "/"))
            }
            context.startActivity(intent)
        }
    }
}
