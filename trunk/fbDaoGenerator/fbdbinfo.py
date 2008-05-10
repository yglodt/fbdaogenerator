#!/usr/bin/python
# -*- coding: iso-8859-1 -*-


import kinterbasdb, time, sys
from optparse import OptionParser


class Table(object):
	def __init__(self):
		self.name = ''
		self.owner = ''
		self.description = ''


class Row(object):
	def __init__(self):
		self.name = ''
		self.type = ''
		self.length = ''
		self.default = ''
		self.null_flag = ''
		self.description = ''


class file2(file):
	# could be done differently, yes I know...
	def write_html_header(self,title):
		global html_header
		return self.write(html_header.replace('$title',title))


def export_table(tablename):
	tablehtmlfile = file2(('%s.html') % (table.name),'w')
	tablehtmlfile.write_html_header('Table Info: %s :: %s' % (options.db_dsn,table.name))
	tablehtmlfile.write("<h4>Table %s</h4>" % (table.name))
	tablehtmlfile.write("<table id='%s'>\n" % (table.name))
	query = "select * from %s" % (table.name)
	sth.execute(query)
	first_line = True
	rowid = 0
	for line in sth:
		colid = 1
		if first_line == True:
			tablehtmlfile.write("<tr id='r%i'>" % (rowid))
			for infofield in sth.description:
				tablehtmlfile.write("<th>%s</th>" % (infofield[0]))
			first_line = False
			tablehtmlfile.write("</tr>\n")
			rowid += 1
		tablehtmlfile.write("<tr id='%i'>" % (rowid))
		for column in line:
			tablehtmlfile.write("<td id='r%ic%i'>%s</td>" % (rowid,colid,column))
			colid += 1
		tablehtmlfile.write("</tr>\n")
		rowid += 1
	tablehtmlfile.write("</table>\n")
	tablehtmlfile.write("</body>\n")
	tablehtmlfile.write("</html>\n")
	tablehtmlfile.close()


html_header = """
<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>
<html xmlns='http://www.w3.org/1999/xhtml'>
<head>
	<!--<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1' />-->
	<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
	<title>$title</title>
	<meta http-equiv='expires' content='0' />
	<meta http-equiv='cache-control' content='no-cache' />
	<style type='text/css'>
	body,p,th,td,li,span,h1,h2,h3,h4,h5,h6,form,input,select,textarea,pre {
		font-family:Verdana,Arial,sans-serif;
		color:#000;
		font-weight:normal;
		font-style:normal;
		text-decoration:none;
		vertical-align:middle;
	}
	p,a,th,td,li,span,form,input,select,textarea,pre {
		font-size:11px;
	}
	table {
		border:1px solid #000;
		border-collapse:collapse;
	}
	th {
		font-weight:bold;
		background-color:#ddd;
		border: 1px solid #000;
		padding:3px;
	}
	td {
		border:1px solid #000;
		padding:3px;
	}
	a.tabletitle {
		font-size:120%;
		text-decoration:none;
		color:#000;
	}
	</style>
</head>
<body>
"""


parser = OptionParser()
parser.add_option('-o', '--output-file',
		dest='outputfile',
		default='index.html',
		type='string',
		metavar='Outputfile',
		help='xhtml index file to be generated. Default: index.html')
parser.add_option('-a', '--alias',
		dest='db_dsn',
		default=None,
		type='string',
		metavar='Alias',
		help='Servername + Database path or alias')
parser.add_option('-u', '--user',
		dest='db_user',
		default='SYSDBA',
		type='string',
		metavar='User',
		help='Firebird User for connecting to the given Database. Default: SYSDBA')
parser.add_option('-p', '--pass',
		dest='db_pass',
		default='masterkey',
		type='string',
		metavar='Password',
		help='Password of the given Firebird User. Default: masterkey')
parser.add_option('-c', '--charset',
		dest='db_charset',
		default='ISO8859_1',
		type='string',
		metavar='Charset',
		help='Database Charset. Default: ISO8859_1')
parser.add_option('-d', '--dialect',
		dest='db_dialect',
		default=1,
		type='int',
		metavar='Dialect',
		help='Firebird SQL Dialect. Default: 1')
parser.add_option('-e', '--export-tables',
		dest='export',
		default=None,
		action='store_true',
		help='Export each table in the DB to an xhtml file (TABLENAME.html)')
parser.add_option('-E', '--export-tables-except',
		dest='export_except',
		default=None,
		type='string',
		metavar='List',
		help='Export all the tables, except the ones in this comma-separated list')
parser.add_option('-I', '--export-only-tables',
		dest='export_only',
		metavar='List',
		default=None,
		help='Export only the given comma-separated list of tables')
parser.add_option('-D', '--delete-html-files',
		dest='delete_html_files',
		action='store_true',
		help='Remove ALL .html files in the current folder before processing')
parser.add_option('-q', '--quiet',
		dest='quiet',
		action='store_true',
		default=False,
		help="Don't print out anything, operate quietly")


############################################################################## Main program starts here
(options, args) = parser.parse_args()


if options.db_dsn == None:
	print "--alias parameter can not be empty, aborting..."
	sys.exit()

if options.export_only and options.export_except:
	print "--export-only-tables and --export-tables-except exclude each other"
	sys.exit()


dbh = kinterbasdb.connect(dsn=options.db_dsn, user=options.db_user, password=options.db_pass, charset=options.db_charset, dialect=1)
sth = dbh.cursor()


if options.delete_html_files == True:
	import glob, os
	filelist = glob.glob('*.html')
	for file in filelist:
		os.unlink(file)


htmlfile = file2(options.outputfile,'w')
htmlfile.write_html_header('Firebird Database Info')


# copy-paste from http://kinterbasdb.sourceforge.net/dist_docs/usage.html#adv_prog_maint_database_info :
bytesInUse = dbh.database_info(kinterbasdb.isc_info_current_memory, 'i')
#print 'The server is currently using %d bytes of memory.' % bytesInUse
buf = dbh.database_info(kinterbasdb.isc_info_db_id, 's')
beginningOfFilename = 2
lengthOfFilename = kinterbasdb.raw_byte_to_int(buf[1])
filename = buf[beginningOfFilename:beginningOfFilename + lengthOfFilename]
beginningOfHostName = (beginningOfFilename + lengthOfFilename) + 1
lengthOfHostName = kinterbasdb.raw_byte_to_int(buf[beginningOfHostName - 1])
host = buf[beginningOfHostName:beginningOfHostName + lengthOfHostName]
#print 'We are connected to the database at %s on host %s.' % (filename, host)


htmlfile.write("<h1>Database Info</h1>\n")
htmlfile.write("<table>\n")
htmlfile.write("<tr><th>Database Server</th><td>%s</td></tr>" % (host))
htmlfile.write("<tr><th>Local database alias</th><td>%s</td></tr>" % (options.db_dsn))
htmlfile.write("<tr><th>Filename</th><td>%s</td></tr>" % (filename))
htmlfile.write("<tr><th>Page created</th><td>%s</td></tr>" % (time.ctime()))
htmlfile.write("</table>\n")


# Get list of all the tables in the DB
alltables = [] # initialize list of "Table" objects
query = """
select RDB$RELATION_NAME,RDB$OWNER_NAME,RDB$DESCRIPTION
from RDB$RELATIONS
where RDB$SYSTEM_FLAG = 0
order by RDB$RELATION_NAME"""
htmlfile.write("<h1>Tables in Database</h1>\n")
htmlfile.write("<table id='all_tables'>\n")
htmlfile.write("<tr><th>Name</th><th>Owner</th><th>Description</th></tr>\n")
sth.execute(query,)
for row in sth:
	table = Table()
	table.name = row[0].strip()
	table.owner = row[1].strip()
	table.description = str(row[2]).strip()
	alltables.append(table)
	htmlfile.write("<tr><td><a href='#%s'>%s</a></td><td>%s</td><td>%s</td></tr>\n" % (table.name, table.name, table.owner, table.description))
htmlfile.write("</table>\n")


# Print the detail
htmlfile.write("<h1>Tables Detail</h1>\n")
for table in alltables:
	if options.quiet == False:
		print 'Creating Index... Table:', table.name

	if (options.export == True) or (options.export_only != None and table.name in options.export_only) or (options.export_except != None and table.name not in options.export_except):
		htmlfile.write("<br /><br /><a href='%s.html' class='tabletitle'>%s</a>\n" % (table.name,table.name))
		if options.quiet == False:
			print "Exporting table", table.name
		export_table(table.name)
	else:
		htmlfile.write("<h4>%s</h4>\n" % (table.name))

	htmlfile.write("<table id='%s'>\n" % (table.name))
	htmlfile.write("<tr><th>Column</th><th>Type</th><th>Length</th><th>Default</th><th>NULL</th><th>Description</th></tr>\n")
	# Column info
	query = """
	select RF.RDB$FIELD_NAME, T.RDB$TYPE_NAME, F.RDB$CHARACTER_LENGTH, RF.RDB$DEFAULT_VALUE, RF.RDB$NULL_FLAG, F.RDB$DESCRIPTION
	from RDB$RELATION_FIELDS RF
	left join RDB$FIELDS F on (RF.RDB$FIELD_SOURCE = F.RDB$FIELD_NAME)
	left join RDB$TYPES T on (F.RDB$FIELD_TYPE = T.RDB$TYPE) and (T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE')
	where RDB$RELATION_NAME = ?
	and F.RDB$SYSTEM_FLAG = 0
	order by RDB$FIELD_POSITION"""
	sth.execute(query,(table.name,))
	#sth.set_type_trans_in({'BLOB': lambda u: u.encode('UTF-8')})
	#sth.set_type_trans_out({'BLOB': lambda s: s.decode('UTF-8')})
	for line in sth:
		row = Row()
		row.name = line[0].strip()
		row.type = line[1].strip()
		row.length = str(line[2]).strip()
		row.default = str(line[3]).strip()
		row.null_flag = line[4]
		if row.null_flag == 1:
			row.null_flag = 'No'
		else:
			row.null_flag = 'Yes'
		row.description = str(line[5]).strip()
		htmlfile.write("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n" % (row.name, row.type, row.length, row.default, row.null_flag, row.description))
	# Primary Keys
	pkquery = """
	select RC.RDB$CONSTRAINT_NAME, S.RDB$FIELD_NAME AS FIELDNAME
	from RDB$RELATION_CONSTRAINTS RC
	left join RDB$INDEX_SEGMENTS S on (S.RDB$INDEX_NAME = RC.RDB$INDEX_NAME)
	left join RDB$RELATION_FIELDS RF on (RF.RDB$FIELD_NAME = S.RDB$FIELD_NAME)
	left join RDB$FIELDS F on (F.RDB$FIELD_NAME = RF.RDB$FIELD_SOURCE)
	left join RDB$TYPES T on (T.RDB$TYPE = F.RDB$FIELD_TYPE)
	where RC.RDB$RELATION_NAME = ?
	and RF.RDB$RELATION_NAME = ?
	and T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE'
	and RC.RDB$CONSTRAINT_TYPE = 'PRIMARY KEY'
	order by S.RDB$FIELD_POSITION"""
	pk_fields = ''
	pk_name = ''
	htmlfile.write("<tr><th colspan='6'>Primary Key</th></tr><tr><td colspan='6'>")
	sth.execute(pkquery,(table.name,table.name))
	for line in sth:
		pk_name = line[0].strip()
		pk_fields += "%s, " % (line[1].strip())
	pk_fields = pk_fields[0:-2]
	htmlfile.write("%s (%s)</td></tr>" % (pk_fields, pk_name))

	# Unique constraints
	# Indexes
	htmlfile.write("</table>\n")


htmlfile.write("</body>\n")
htmlfile.write("</html>\n")
htmlfile.close()
