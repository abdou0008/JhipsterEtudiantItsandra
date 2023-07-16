import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INiveau } from '../niveau.model';
import { NiveauService } from '../service/niveau.service';

@Component({
  templateUrl: './niveau-delete-dialog.component.html',
})
export class NiveauDeleteDialogComponent {
  niveau?: INiveau;

  constructor(protected niveauService: NiveauService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.niveauService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
