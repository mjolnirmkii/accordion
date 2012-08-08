/** Overrides the accordion save method for accordions with file input, 
 *  in a separate js file since the save method for those is 
 *  significantly more complex, requires more synchronization, 
 *  and requires jQuery 1.5
 *  
 *  Author: Chase Putnam
 */

$(function() {
	function delayUnblock() {
		var oldUnblock = $.unblockUI;
		$.unblockUI = function() {}; // no-op
		var numIframes = $('.accordion_fileIframe').length;
		function iframeLoaded() {
			numIframes--;
			if (!numIframes) {
				$.unblockUI = oldUnblock; 
				$.unblockUI();
				$('.accordionContainer').unbind('accordion_fileUploadFailed');
				$('.accordionContainer').unbind('accordion_fileUploadComplete');
			}
		}
		if (numIframes) {
			$('.accordionContainer').bind('accordion_fileUploadFailed', iframeLoaded);
			$('.accordionContainer').bind('accordion_fileUploadComplete', iframeLoaded);
		}
		else {
			$.unblockUI = oldUnblock;
		}
	}
	$('.accordionContainer').bind({accordion_editLoaded : delayUnblock, accordion_newLoaded : delayUnblock });
});

function accordion_replace2(accordion, data, status, xhr) {
	var dfd = $.Deferred();
	var iframes = accordion.find('.accordion_fileIframe').filter(function(id, element) { 
		if ($(element).contents().find('input[type="file"]').val().length)
			return true;
	});
	AccordionReplacer(dfd, accordion, data, iframes); // setup listeners or just replace...
	
	// if no validation errors then we trigger any fileuploads before overwriting the accordion.
	if (typeof(xhr) === 'undefined' || xhr.getResponseHeader('Accordion-Validation-Error') !== 'true') {
		dfd.fail(function() {
			alert('One or more file uploads failed. Please try uploading again.');
		});
		iframes.first().contents().find('form').submit();
	}
	return dfd.promise();
}

function AccordionReplacer(dfd, accordion, data, iframes) {
	this.dfd = dfd;
	var failCount = 0;
	accordion.bind('accordion_fileUploadComplete', function(event) {
		iframes = iframes.not(event.target);
		if (iframes.length == 0) { // last success/failure
			var promiseArgs = {accordion: accordion, resave: true};
			if (failCount == 0) { // no failures
				dfd.resolveWith(promiseArgs);
			}
			else {
				dfd.rejectWith(promiseArgs);
			}
		}
		else { // upload next file
			iframes.first().contents().find('form').submit();
		}
	});
	accordion.bind('accordion_fileUploadFailed', function(event) {
		failCount++;
		iframes = iframes.not(event.target);
		if (iframe.length == 0) {
			var promiseArgs = {accordion: accordion, resave: true};
			dfd.rejectWith(promiseArgs);
		}
		else { // upload next file
			iframes.first().contents().find('form').submit();
		}
	});
	if (iframes.length == 0) { // no file inputs on this accordion
		var container = accordion.parent();
		accordion.replaceWith(data);
		var newAccordion = container.children('.accordion:not(.ui-accordion)');
		var promiseArgs = {accordion: newAccordion, resave: false};
		dfd.resolveWith(promiseArgs);
	}
	return dfd.promise();
}

accordion_cancelNew = accordion_delete; /* remap cancelNew to delete since determining whether this object is persistent is significantly more complex now. */

/* Loads the display-only accordion content area after validation/saving of editable area */
function accordion_save(event) {
	var accordion = $(event.target).parents('.accordion').first();
	if (accordion.children().children('.accordion_content').find('.accordion_editable').length) {
		alert('Please save nested items first.');
		return;
	}
	var args = accordion.parents('.accordionContainer').first().find('.ajaxParams').first().children().serialize();
	var newAccordion = accordion.children('.accordion_new').length;
	args += '&' + accordion.find('input, textarea, select').serialize();
	args += '&accordion.itemId=' + accordion.find('.itemId').val();
	var url = accordion_saveUrl + '_' + accordion.parents('.accordionContainer').first().find('.ajaxParams').first().children('input[name="accordion.id"]').val();
	$.blockUI();
	accordion.trigger('accordion_savedLoading');
	accordion.trigger('accordion_loading');
	$.post(url, args, function(data, status, xhr) {
		accordion_replace2 (accordion, data, status, xhr).done(function() {
			accordion = this.accordion;
			if (this.resave) {
				accordion_save({target: accordion.children().first()});
				return;
			}
			accordion.accordion({collapsible: true, autoHeight: false});
			if (accordion.find('.accordion_saveButton').length) { // Save failed
				if (newAccordion) {
					accordion.children().addClass('accordion_new'); // tag this accordion as new
					accordion.children().addClass('accordion_editable'); // tag accordion as editable
				}
				accordion.find('.accordion_editLink').first().html('Cancel');
				if (accordion.children('.accordion_new').length) {
					accordion.find('.accordion_editLink').first().click(accordion_cancelNew);
				}
				else {
					accordion.find('.accordion_editLink').first().click(accordion_cancelEdit);
				}
				accordion.find('.accordion_saveButton').first().click(accordion_save);
			}
			else { // Save succeeded
				accordion.find('.accordion_editLink').first().click(accordion_edit);
			}
			accordion.find('.accordion_deleteLink').first().click(accordion_delete);
			accordion.trigger('accordion_savedLoaded');
			accordion.trigger('accordion_loaded');
			$.unblockUI();
		});
	});
}