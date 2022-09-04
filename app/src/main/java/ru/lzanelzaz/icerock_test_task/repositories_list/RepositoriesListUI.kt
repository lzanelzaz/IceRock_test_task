package ru.lzanelzaz.icerock_test_task.repositories_list

fun getRecyclerViewAdapter(state: RepositoriesListViewModel.State): ReposListAdapter {
    val myRecyclerViewAdapter = ReposListAdapter()
    val dataset = if (state is RepositoriesListViewModel.State.Loaded)
        state.repos
    else
        emptyList()
    myRecyclerViewAdapter.submitList(dataset)
    return myRecyclerViewAdapter
}