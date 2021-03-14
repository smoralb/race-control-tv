package fr.groggy.racecontrol.tv.ui.season.archive

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.f1tv.F1TvSeasonId
import fr.groggy.racecontrol.tv.ui.season.browse.SeasonBrowseActivity

@Keep
@AndroidEntryPoint
class SeasonArchiveFragment: BrowseSupportFragment(), OnItemViewClickedListener {
    private val itemAdapter: ArrayObjectAdapter by lazy {
        ArrayObjectAdapter(ArchivePresenter())
    }

    private val moreAdapter: ArrayObjectAdapter by lazy {
        ArrayObjectAdapter(SettingsPresenter()).apply {
            setItems(listOf(
                1
            ), null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        adapter = ArrayObjectAdapter(ListRowPresenter()).apply {
            setItems(listOf(
                ListRow(HeaderItem(SEASON_SELECT_ROW_ID, getString(R.string.choose_a_season)), itemAdapter),
                ListRow(HeaderItem(MORE_ROW_ID, getString(R.string.more)), moreAdapter)
            ), null)
        }
        onItemViewClickedListener = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: SeasonArchiveViewModel by viewModels()
        lifecycleScope.launchWhenStarted {
            val archives = viewModel.listArchive()
            itemAdapter.setItems(archives, null)
        }
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        when (row?.id) {
            SEASON_SELECT_ROW_ID -> {
                val archiveItem = item as Archive
                val browseActivity = SeasonBrowseActivity
                    .intent(requireContext(), F1TvSeasonId.ofUid(archiveItem.uid))
                startActivity(browseActivity)
            }
        }
    }

    companion object {
        private const val SEASON_SELECT_ROW_ID = 1L
        private const val MORE_ROW_ID = 2L
    }
}
