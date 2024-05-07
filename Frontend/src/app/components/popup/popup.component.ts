import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-popup',
  templateUrl: './popup.component.html',
  styleUrls: ['./popup.component.css']
})
export class PopupComponent {
  @Output() confirmAction: EventEmitter<void> = new EventEmitter<void>();

  constructor(public dialogRef: MatDialogRef<PopupComponent>) {}

  confirm(): void {
    this.confirmAction.emit();
    this.dialogRef.close();
  }

  cancel(): void {
    this.dialogRef.close();
  }
}