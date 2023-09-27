package com.kirillyemets.catapp.ui.screens.animalinfo

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kirillyemets.catapp.ui.navigation.localNavController
import com.kirillyemets.catapp.ui.screens.search.BreedInfoScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalInfoModalBottomSheet(
    animalId: String,
    actionBar: @Composable () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val navController = localNavController()

    ModalBottomSheet(
        modifier = Modifier.padding(top = 32.dp),
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        AnimalInfoBottomSheetScreen(
            animalId = animalId,
            navigateToBreedInfo = { breedId ->
                navController.navigate(BreedInfoScreenDestination.route(breedId))
            },
            actionBar = actionBar
        )
    }
}