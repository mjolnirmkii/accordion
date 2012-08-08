/** Customized accordion functions. 
 *
 * Author: Chase Putnam
 */
var accordion_deleteUrl = window.location.pathname + '?deleteAccordion';
var accordion_newUrl = window.location.pathname + '?newAccordion';
var accordion_editUrl = window.location.pathname + '?editAccordion';
var accordion_saveUrl = window.location.pathname + '?saveAccordion';
var accordion_displayUrl = window.location.pathname + '?displayAccordion';

var accordion_deleteMessage = null;

$(function() {
	$('.accordion').accordion({collapsible: true, autoHeight: false, active: false});
	$('.accordion_expandAllLink').click(accordion_expandAll);
	$('.accordion_collapseAllLink').click(accordion_collapseAll);
	$('.accordion_editLink').click(accordion_edit);
	$('.accordion_deleteLink').click(accordion_delete);
	$('.accordion_addButton').click(accordion_add);
	$('.accordionContainer').each(function(id, element) {
		if (!$(element).find('.accordion').length) {
			$(element).find('.accordion_expandCollapse').hide();
		}
	});
	$('form:not(.accordion_doNotWarn)').submit(accordion_checkEditable);
	$('.actionBarButton div a, #actionBar a').click(accordion_checkEditable);
	$('.accordionContainer').bind('accordion_loaded', function(event) {
		$(window).resize();
		var accordion = $(event.target);
		accordion.find('.accordion').accordion({collapsible: true, autoHeight: false, active: false});
		accordion.find('.accordion_expandAllLink').click(accordion_expandAll);
		accordion.find('.accordion_collapseAllLink').click(accordion_collapseAll);
		accordion.find('.accordion_editLink').slice(1).click(accordion_edit);
		accordion.find('.accordion_deleteLink').slice(1).click(accordion_delete);
		accordion.find('.accordion_addButton').click(accordion_add);
		accordion.find('.accordionContainer').each(function(id, element) {
			if (!$(element).find('.accordion').length) {
				$(element).find('.accordion_expandCollapse').hide();
			}
		});
	});
});

/* Utility function for replacing an accordion. Replaces and returns a reference to replacement */
function accordion_replace(accordion, data) {
	var container = accordion.parent();
	accordion.replaceWith(data);
	return container.children('.accordion:not(.ui-accordion)');
}

/* Utility function that checks for editable accordions. Alerts and cancels event propagation if found. 
 *  To be called before doing things that would make the user lose unsaved accordions */
function accordion_checkEditable(event) {
	if ($('.accordion_editable').length) {
		$.unblockUI();
		alert('You have unsaved items on this page. Please save them or cancel changes before attempting this action.');
		event.stopImmediatePropagation();
		return false;
	}
}

/* Expands an accordion if it is collapsed */
function accordion_expand(id, accordion) {
	if ($(accordion).find('.ui-accordion-header span').first().hasClass('ui-icon-triangle-1-e')) {
		$(accordion).accordion('option', 'active', 0);
	}
}

/* Collapses an accordion if it is expanded */
function accordion_collapse(id, accordion) {
	if ($(accordion).find('.ui-accordion-header span').first().hasClass('ui-icon-triangle-1-s')) {
		$(accordion).accordion('option', 'active', false);
	}
}

/* Expand all accordions in the given accordion area */
function accordion_expandAll(event) {
	var container = $(event.target).parents('.accordionContainer').first();
	container.children('.accordions').children('.accordion').each(accordion_expand);
	return false;
}

/* Collapse all accordions in the given accordion area */
function accordion_collapseAll(event) {
	var container = $(event.target).parents('.accordionContainer').first();
	container.children('.accordions').children('.accordion').each(accordion_collapse);
	return false;
}

/* Loads the editable accordion content area. To be called by user edit attempts. Alerts if there are any open items. */
function accordion_edit(event) {
	var accordionContainer = $(event.target).parents('.accordionContainer').first();
	if (accordionContainer.find('.accordion_editable').length) {
		alert('Please save any open items first.');
		return false;
	}
	accordion_edit_unchecked(event);
	return false;
}

/* Loads the editable accordion content area. Can be used to refresh the area */
function accordion_edit_unchecked(event) {
	var accordion = $(event.target).parents('.accordion').first();
	var args = accordion.parents('.accordionContainer').first().find('.ajaxParams').first().children().serialize();
	args += '&accordion.itemId=' + accordion.find('.itemId').val();
	var url = accordion_editUrl;
	$.blockUI();
	accordion.trigger('accordion_editLoading');
	accordion.trigger('accordion_loading');
	$.post(url, args, function(data) {
		accordion = accordion_replace (accordion, data);
		var newAccordion = accordion.find('.isNew').val() === 'true';
		accordion.accordion({collapsible: true, autoHeight: false});
		accordion.children().addClass('accordion_editable');
		if (newAccordion) {
			accordion.find('.accordion_editLink').first().click(accordion_cancelNew);
			accordion.find('.accordion_deleteLink').first().click(accordion_cancelNew);
		}
		else {
			accordion.find('.accordion_editLink').first().click(accordion_cancelEdit);
			accordion.find('.accordion_deleteLink').first().click(accordion_delete);
		}
		accordion.find('.accordion_editLink').first().html('Cancel');
		accordion.find('.accordion_saveButton').click(accordion_save);
		accordion.trigger('accordion_editLoaded');
		accordion.trigger('accordion_loaded');
		$.unblockUI();
	});
	return false;
}

/* Like accordion_edit_unchecked, except it passes form data through the request */
function accordion_partialEdit(event) {
	var accordion = $(event.target).parents('.accordion').first();
	var args = accordion.parents('.accordionContainer').first().find('.ajaxParams').first().children().serialize();
	args += '&accordion.itemId=' + accordion.find('.itemId').val();
	args += '&' + accordion.find('form').serialize();
	var url = accordion_editUrl;
	$.blockUI();
	accordion.trigger('accordion_editLoading');
	accordion.trigger('accordion_loading');
	$.post(url, args, function(data) {
		accordion = accordion_replace (accordion, data);
		var newAccordion = accordion.find('.isNew').val() === 'true';
		accordion.accordion({collapsible: true, autoHeight: false});
		if (newAccordion) {
			accordion.find('.accordion_editLink').first().click(accordion_cancelNew);
			accordion.find('.accordion_deleteLink').first().click(accordion_cancelNew);
		}
		else {
			accordion.find('.accordion_editLink').first().click(accordion_cancelEdit);
			accordion.find('.accordion_deleteLink').first().click(accordion_delete);
		}
		accordion.children().addClass('accordion_editable');
		accordion.find('.accordion_editLink').first().html('Cancel');
		accordion.find('.accordion_saveButton').click(accordion_save);
		accordion.trigger('accordion_editLoaded');
		accordion.trigger('accordion_loaded');
		$.unblockUI();
	});
	return false;
}

/* Cancels an edit on an accordion */
function accordion_cancelEdit(event) {
	if (!confirm('Are you sure you want to cancel edits? You will lose any changes.')) {
		return false;
	}
	var accordion = $(event.target).parents('.accordion').first();
	var args = accordion.parents('.accordionContainer').first().find('.ajaxParams').first().children().serialize();
	args += '&accordion.itemId=' + accordion.find('.itemId').val();
	var url = accordion_displayUrl;
	$.blockUI();
	accordion.trigger('accordion_displayLoading');
	accordion.trigger('accordion_loading');
	$.post(url, args, function(data) {
		accordion = accordion_replace (accordion, data);
		accordion.accordion({collapsible: true, autoHeight: false});
		accordion.find('.accordion_editLink').first().click(accordion_edit);
		accordion.find('.accordion_deleteLink').first().click(accordion_delete);
		accordion.find('.accordion_saveButton').first().click(accordion_save);
		accordion.trigger('accordion_displayLoaded');
		accordion.trigger('accordion_loaded');
		$.unblockUI();
	});
	return false;
}

/* Loads the display-only accordion content area after validation/saving of editable area */
function accordion_save(event) {
	var accordion = $(event.target).parents('.accordion').first();
	if (accordion.children().children('.accordion_content').find('.accordion_editable').length) {
		alert('Please save nested items first.');
		return;
	}
	var args = accordion.parents('.accordionContainer').first().find('.ajaxParams').first().children().serialize();
	args += '&' + accordion.find('input, textarea, select').serialize();
	args += '&accordion.itemId=' + accordion.find('.itemId').val();
	var url = accordion_saveUrl + '_' + accordion.parents('.accordionContainer').first().find('.ajaxParams').first().children('input[name="accordion.id"]').val();
	$.blockUI();
	accordion.trigger('accordion_savedLoading');
	accordion.trigger('accordion_loading');
	$.post(url, args, function(data) {
		accordion = accordion_replace (accordion, data);
		var newAccordion = accordion.find('.isNew').val() === 'true';
		accordion.accordion({collapsible: true, autoHeight: false});
		if (accordion.find('.accordion_saveButton').length) { // Save failed
			accordion.children().addClass('accordion_editable'); // tag accordion as editable
			accordion.find('.accordion_editLink').first().html('Cancel');
			if (newAccordion) {
				accordion.find('.accordion_editLink').first().click(accordion_cancelNew);
				accordion.find('.accordion_deleteLink').first().click(accordion_cancelNew);
			}
			else {
				accordion.find('.accordion_editLink').first().click(accordion_cancelEdit);
				accordion.find('.accordion_deleteLink').first().click(accordion_delete);
			}
			accordion.find('.accordion_saveButton').first().click(accordion_save);
		}
		else { // Save succeeded
			accordion.find('.accordion_editLink').first().click(accordion_edit);
			accordion.find('.accordion_deleteLink').first().click(accordion_delete);
		}
		accordion.trigger('accordion_savedLoaded');
		accordion.trigger('accordion_loaded');
		$.unblockUI();
	});
}

/* Removes the accordion object */
function accordion_delete(event) {
	var message = accordion_deleteMessage || 'Are you sure you want to remove this item?';
	if (!confirm(message)) {
		return false;
	}
	var accordion = $(event.target).parents('.accordion').first();
	var accordionContainer = accordion.parents('.accordionContainer').first();
	var accordions = accordion.parents('.accordions').first();
	var itemId = accordion.find('.itemId').val();
	var url = accordion_deleteUrl;
	var args = accordionContainer.find('.ajaxParams').first().children().serialize();
	args += '&accordion.itemId=' + itemId;
	$.blockUI();
	accordion.trigger('accordion_deleting');
	$.post(url, args, function(data) {
		if (data == 'success') {
			accordion.remove();
			// Fix indices
			var params = accordionContainer.find('.ajaxParams').first();
			var collectionName = params.children('input[name="accordion.collectionName"]').val();
			accordions.children().each(function(id, element) {
				var accordion = $(element);
				var index = accordion.find('.itemId').first().val();
				if (index > itemId) {
					accordion.find('.itemId').first().val(index - 1); // decrement
					var prefixExpression = new RegExp('^' + collectionName + '\\[' + index + '\\]');
					accordion.find('input,select,textarea').each(function(id, element) {
						var name = $(element).attr('name');
						name = name.replace(prefixExpression, collectionName + '[' + (index - 1) + ']');
						$(element).attr('name', name);
					});
				}
			});
			accordions.trigger('accordion_removed');
		}
		else {
			alert ("Error removing item. Please try again.");
			accordion.trigger('accordion_deleteFailed');
		}
		if (!$(accordionContainer).find('.accordion').length) {
			$(accordionContainer).find('.accordion_expandCollapse').hide();
		}
		$.unblockUI();
	});
	return false;
}

/* TODO: change this behavior? 
 * Current behavior is to remove accordion after confirm
 */
function accordion_cancelNew(event) {
	var accordion = $(event.target).parents('.accordion').first();
	var accordionContainer = accordion.parents('.accordionContainer').first();
	if (!confirm('Are you sure you want to remove this item?')) {
		return false;
	}
	accordion.remove();
	if (!accordionContainer.find('.accordion').length) {
		accordionContainer.find('.accordion_expandCollapse').first().hide();
	}
	return false;
}

/* Adds a new form area (accordion section) */
function accordion_add(event) {
	accordion_collapseAll(event); // collapse all existing accordions
	var accordionContainer = $(event.target).parents('.accordionContainer').first();
	if (accordionContainer.find('.accordion_editable').length) {
		alert('Please save any open items first.');
		return;
	}
	accordionContainer.find('.accordion_expandCollapse').first().show();
	var args = accordionContainer.find('.ajaxParams').first().children().serialize();
	var url = accordion_newUrl;
	$.blockUI();
	accordionContainer.trigger('accordion_newLoading');
	$.post(url, args, function(data) {
		var accordion = accordionContainer.find('.accordions').first().append(data).children().last(); // the new accordion
		accordion.accordion({collapsible: true, autoHeight: false});
		accordion.children().addClass('accordion_editable'); // tag this accordion as editable
		accordion.find('.accordion_editLink').first().html('Cancel'); // Change edit text to cancel
		accordion.find('.accordion_editLink').first().click(accordion_cancelNew);
		accordion.find('.accordion_deleteLink').first().click(accordion_cancelNew);
		accordion.find('.accordion_saveButton').first().click(accordion_save);
		accordion.trigger('accordion_newLoaded');
		accordion.trigger('accordion_loaded');
		$.unblockUI();
	});
}