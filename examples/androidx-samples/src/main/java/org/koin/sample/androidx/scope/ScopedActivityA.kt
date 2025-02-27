package org.koin.sample.androidx.scope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.scoped_activity_a.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.RetainedScopeActivity
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.scope.createActivityRetainedScope
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.sample.android.R
import org.koin.sample.androidx.components.*
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.components.scope.SessionActivity
import org.koin.sample.androidx.di.scopeModuleActivityA
import org.koin.sample.androidx.utils.navigateTo

class ScopedActivityA : RetainedScopeActivity(R.layout.scoped_activity_a) {

    // Inject from current scope
    val currentSession by inject<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assert(currentSession == get<Session>())
        if (SESSION_ID_VAR.isEmpty()) {
            println("Create ID for session: $SESSION_ID_VAR")
            SESSION_ID_VAR = currentSession.id
        }
        assert(SESSION_ID_VAR == currentSession.id)

        // Conpare different scope instances
        val scopeSession1 = getKoin().createScope(SESSION_1, named(SCOPE_ID))
        val scopeSession2 = getKoin().createScope(SESSION_2, named(SCOPE_ID))
        assert(scopeSession1.get<Session>(named(SCOPE_SESSION)) != currentSession)
        assert(
            scopeSession1.get<Session>(named(SCOPE_SESSION)) != scopeSession2.get<Session>(
                named(
                    SCOPE_SESSION
                )
            )
        )

        // set data in scope SCOPE_ID
        val session =
            getKoin().getOrCreateScope(SCOPE_ID, named(SCOPE_ID)).get<Session>(named(SCOPE_SESSION))
        session.id = ID

        title = "Scope Activity A"

        scoped_a_button.setOnClickListener {
            navigateTo<ScopedActivityB>(isRoot = true)
        }

        scopeSession1.close()
        scopeSession2.close()
    }

    companion object {
        var SESSION_ID_VAR = ""
    }
}