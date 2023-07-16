import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FiliereComponent } from './list/filiere.component';
import { FiliereDetailComponent } from './detail/filiere-detail.component';
import { FiliereUpdateComponent } from './update/filiere-update.component';
import { FiliereDeleteDialogComponent } from './delete/filiere-delete-dialog.component';
import { FiliereRoutingModule } from './route/filiere-routing.module';

@NgModule({
  imports: [SharedModule, FiliereRoutingModule],
  declarations: [FiliereComponent, FiliereDetailComponent, FiliereUpdateComponent, FiliereDeleteDialogComponent],
  entryComponents: [FiliereDeleteDialogComponent],
})
export class FiliereModule {}
