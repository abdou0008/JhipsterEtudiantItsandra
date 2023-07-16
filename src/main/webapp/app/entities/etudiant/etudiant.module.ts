import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EtudiantComponent } from './list/etudiant.component';
import { EtudiantDetailComponent } from './detail/etudiant-detail.component';
import { EtudiantUpdateComponent } from './update/etudiant-update.component';
import { EtudiantDeleteDialogComponent } from './delete/etudiant-delete-dialog.component';
import { EtudiantRoutingModule } from './route/etudiant-routing.module';

@NgModule({
  imports: [SharedModule, EtudiantRoutingModule],
  declarations: [EtudiantComponent, EtudiantDetailComponent, EtudiantUpdateComponent, EtudiantDeleteDialogComponent],
  entryComponents: [EtudiantDeleteDialogComponent],
})
export class EtudiantModule {}
